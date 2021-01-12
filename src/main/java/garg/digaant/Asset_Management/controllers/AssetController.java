package garg.digaant.Asset_Management.controllers;

import garg.digaant.Asset_Management.maps.AssetMapService;
import garg.digaant.Asset_Management.models.Asset;
import garg.digaant.Asset_Management.models.AssetDetail;
import garg.digaant.Asset_Management.models.AssetStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class AssetController {
    @Autowired
    AssetMapService assetMapService = new AssetMapService();

    @GetMapping("/assets")
    private Set<Asset> getAllAssets(){
        return assetMapService.findAll();
    }

    @GetMapping("/assets/{id}")
    private Asset getAssetById(@PathVariable("id") Long id){
        return assetMapService.findById(id);
    }

    @DeleteMapping("/assets/{id}")
    private void deleteAssetById(@PathVariable("id") Long id){
        assetMapService.deleteById(id);
    }

    @PostMapping("/assets")
    private Long saveAsset(@RequestBody Asset asset){
        Asset savedAsset = assetMapService.save(asset);
        return savedAsset.getId();
    }

    @RequestMapping("/downloadCSV")
    public void downloadCSVFile(HttpServletResponse response) throws IOException {

        String csvFileName = "output.csv";

        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                csvFileName);
        response.setHeader(headerKey, headerValue);


        String line = "";
        String filePath = "C:\\Users\\Administrator\\Documents\\TCS ING Training\\Spring Framework\\Asset_Tracking_Spring-01\\src\\main\\java\\garg\\digaant\\Asset_Management\\Input.csv";
        BufferedReader fileReader = null;
        List<AssetDetail> assetDetails = new ArrayList<AssetDetail>();
        try {
            fileReader = new BufferedReader(new FileReader(filePath));

            while ((line = fileReader.readLine()) != null) {
                Long assetDetailId = 1L;
                String[] values = line.split(",");
                AssetDetail assetInstance = AssetDetail.builder().id(assetDetailId).assetName(values[0])
                        .startTime(LocalDateTime.parse(values[1], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                        .endTime(LocalDateTime.parse(values[2], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                        .severity(Integer.valueOf(values[3])).build();
                assetDetailId++;
                assetDetails.add(assetInstance);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Set<Asset> assets = assetMapService.findAll();
        Long assetId = 1L;
        for (AssetDetail assetDetail : assetDetails){

            if(assets.isEmpty()){
                Asset asset = Asset.builder().id(assetId).assetName(assetDetail.getAssetName()).build();
                asset.getAssetDetails().add(assetDetail);
                assetDetail.setAsset(asset);
                assetMapService.save(asset);
                assets.add(asset);
                assetId++;
            }
            else{
                int flag = 0;
                for (Asset asset: assets) {
                    if (asset.getAssetName().equals(assetDetail.getAssetName())){
                        asset.getAssetDetails().add(assetDetail);
                        assetDetail.setAsset(asset);
                        flag = 1;
                    }
                }
                if(flag==0){
                    Asset asset = Asset.builder().id(assetId).assetName(assetDetail.getAssetName()).build();
                    asset.getAssetDetails().add(assetDetail);
                    assets.add(asset);
                    assetDetail.setAsset(asset);
                    assetMapService.save(asset);
                    assetId++;
                }
            }
        }
        List<AssetStats> listAssetStats = new ArrayList<AssetStats>();
        for (Asset asset : assets) {
            listAssetStats.add(AssetStats.builder().id(asset.getId())
                    .assetName(asset.getAssetName())
                    .noOfIncidents(assetMapService.totalIncidentsOfAsset(asset.getAssetName()))
                    .totalUpTime(assetMapService.totalUptimeForAsset(asset.getAssetName()))
                    .rating(assetMapService.calculateRating(asset.getAssetName()))
                    .build());
        }

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);

        String[] header = {"assetName", "noOfIncidents", "totalUpTime", "rating"};

        csvWriter.writeHeader(header);

        for (AssetStats assetStat : listAssetStats) {
            csvWriter.write(assetStat, header);
        }

        csvWriter.close();
    }

}
