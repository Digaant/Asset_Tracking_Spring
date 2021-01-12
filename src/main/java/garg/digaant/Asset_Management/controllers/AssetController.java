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


/*
        //Making asset to provide input for Output CSV File.
        Asset asset1 =  Asset.builder().assetName("TTYY889").id(1L).build();
        /*Each Asset has an assetDetails Set, which includes all of its instances as individual AssetDetail
        Class Object. This set(assetDetails) will be used for assetDetails attribute of Asset class object * /
        Set<AssetDetail> assetDetails = new HashSet<>();

        //Creating AssetDetail Objects to insert into assetDetails attribute of Asset Class
        AssetDetail assetDetail1 = AssetDetail.builder().asset(asset1)
                .endTime(LocalDateTime.of(2021,01,10,15,34,22))
                .startTime(LocalDateTime.of(2021, 01, 10, 13, 45, 32))
                .id(1L)
                .severity(2)
                .build();

        AssetDetail assetDetail2 = AssetDetail.builder().asset(asset1)
                .endTime(LocalDateTime.of(2021,01,10,17,37,22))
                .startTime(LocalDateTime.of(2021, 01, 10, 16, 45, 32))
                .id(2L)
                .severity(3)
                .build();

        AssetDetail assetDetail3 = AssetDetail.builder().asset(asset1)
                .endTime(LocalDateTime.of(2021,01,11,15,34,22))
                .startTime(LocalDateTime.of(2021, 01, 11, 13, 45, 32))
                .id(3L)
                .severity(1)
                .build();

        //Adding the all assetDetail objects to assetDetails Set.
        assetDetails.add(assetDetail1);
        assetDetails.add(assetDetail2);
        assetDetails.add(assetDetail3);

        //Setting assetDetails attribute of asset1 to assetDetails Set declared above.
        asset1.setAssetDetails(assetDetails);
        //Saving the asset.
        assetMapService.save(asset1);


        //Repeating the above process for another asset.
        Asset asset2 =  Asset.builder().assetName("TTYY911").id(2L).build();

        Set<AssetDetail> assetDetails1 = new HashSet<>();
        AssetDetail assetDetail4 = AssetDetail.builder().asset(asset2)
                .endTime(LocalDateTime.of(2021,01,10,11,34,22))
                .startTime(LocalDateTime.of(2021, 01, 10, 9, 45, 32))
                .id(4L)
                .severity(1)
                .build();
        AssetDetail assetDetail5 = AssetDetail.builder().asset(asset2)
                .endTime(LocalDateTime.of(2021,01,11,15,34,22))
                .startTime(LocalDateTime.of(2021, 01, 11, 13, 45, 32))
                .id(5L)
                .severity(2)
                .build();
        AssetDetail assetDetail6 = AssetDetail.builder().asset(asset2)
                .endTime(LocalDateTime.of(2021,01,12,15,34,22))
                .startTime(LocalDateTime.of(2021, 01, 12, 13, 45, 32))
                .id(6L)
                .severity(1)
                .build();

        assetDetails1.add(assetDetail4);
        assetDetails1.add(assetDetail5);
        assetDetails1.add(assetDetail6);

        asset2.setAssetDetails(assetDetails1);
        assetMapService.save(asset2);

        //Getting the assetName required to asset Statistics functions
        String assetName1 = assetMapService.findById(1L).getAssetName();

        //Assigning Statistics to attributes of AssetStats class
        AssetStats assetStats1 = AssetStats.builder().assetName(assetName1).id(1L)
                .noOfIncidents(assetMapService.totalIncidentsOfAsset(assetName1))
                .rating(assetMapService.calculateRating(assetName1))
                .totalUpTime(assetMapService.totalUptimeForAsset(assetName1))
                .build();

        //Repeating above for asset2.
        String assetName2 = assetMapService.findById(2L).getAssetName();

        AssetStats assetStats2 = AssetStats.builder().assetName(assetName2).id(2L)
                .noOfIncidents(assetMapService.totalIncidentsOfAsset(assetName2))
                .rating(assetMapService.calculateRating(assetName2))
                .totalUpTime(assetMapService.totalUptimeForAsset(assetName2))
                .build();

        List<AssetStats> listAssetStats = Arrays.asList(assetStats1, assetStats2);
Writing the Statistics to an Output Csv File.
*/
        String line = "";
        String filePath = "C:\\Users\\Administrator\\Documents\\TCS ING Training\\Spring Framework\\Asset_Tracking_Management-master\\src\\main\\java\\garg\\digaant\\Asset_Management\\Input.csv";
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
                    .totalUpTime(assetMapService.totalIncidentsOfAsset(asset.getAssetName()))
                    .rating(assetMapService.totalIncidentsOfAsset(asset.getAssetName()))
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
