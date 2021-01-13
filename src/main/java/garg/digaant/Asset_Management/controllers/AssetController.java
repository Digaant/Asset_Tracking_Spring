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
        List<AssetDetail> assetDetails = new ArrayList<AssetDetail>();//List of all asset instances
        try {
            fileReader = new BufferedReader(new FileReader(filePath));

            while ((line = fileReader.readLine()) != null) {/*Reading in input file*/
                Long assetDetailId = 1L;
                String[] values = line.split(",");//Saving all values in different columns Separated by comma.
                AssetDetail assetInstance = AssetDetail.builder().id(assetDetailId).assetName(values[0])
                        .startTime(LocalDateTime.parse(values[1], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                        .endTime(LocalDateTime.parse(values[2], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                        .severity(Integer.valueOf(values[3])).build(); //adding instance as an assetDetail class object.
                assetDetailId++;
                assetDetails.add(assetInstance);//adding assetDetail class object to assetDetails
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Set<Asset> assets = assetMapService.findAll(); //Getting all assets saved in our service.
        Long assetId = 1L;
        for (AssetDetail assetDetail : assetDetails){//Going through each asset Detail and allocating it to respective asset.

            if(assets.isEmpty()){//if there are no assets existing creating a new one.
                Asset asset = Asset.builder().id(assetId).assetName(assetDetail.getAssetName()).build();
                asset.getAssetDetails().add(assetDetail);//Adding assetDetail to asset.
                assetDetail.setAsset(asset);//Mapping asset to assetDetail.
                assetMapService.save(asset);//Save asset in service.
                assets.add(asset);//add assets to asset list
                assetId++;//Increase AssetId
            }
            else{
                int flag = 0;//flag to check whether asset with AssetName already exists.
                for (Asset asset: assets) {
                    if (asset.getAssetName().equals(assetDetail.getAssetName())){
                        asset.getAssetDetails().add(assetDetail);//If asset with AssetName found, add detail to it.
                        assetDetail.setAsset(asset);//Mapping asset to assetDetail.
                        flag = 1;//asset with assetName found.
                    }
                }
                if(flag==0){//asset with assetName not found. Creating a new asset object with assetName.
                    Asset asset = Asset.builder().id(assetId).assetName(assetDetail.getAssetName()).build();
                    asset.getAssetDetails().add(assetDetail);
                    assets.add(asset);
                    assetDetail.setAsset(asset);
                    assetMapService.save(asset);
                    assetId++;
                }
            }
        }
        List<AssetStats> listAssetStats = new ArrayList<AssetStats>();//Creating a list of Statistics for each Asset.
        for (Asset asset : assets) {
            listAssetStats.add(AssetStats.builder().id(asset.getId())
                    .assetName(asset.getAssetName())
                    .noOfIncidents(assetMapService.totalIncidentsOfAsset(asset.getAssetName()))
                    .totalUpTime(assetMapService.totalUptimeForAsset(asset.getAssetName()))
                    .rating(assetMapService.calculateRating(asset.getAssetName()))
                    .build());
        }

        //Writing to the CSV output file.
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
