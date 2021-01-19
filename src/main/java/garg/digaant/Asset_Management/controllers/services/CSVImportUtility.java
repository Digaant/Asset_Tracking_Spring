package garg.digaant.Asset_Management.controllers.services;
import garg.digaant.Asset_Management.maps.AssetMapService;
import garg.digaant.Asset_Management.models.Asset;
import garg.digaant.Asset_Management.models.AssetDetail;
import garg.digaant.Asset_Management.models.AssetStats;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
@Service
@Component
public class CSVImportUtility {
    private AssetMapService assetMapService;
    public CSVImportUtility(AssetMapService assetMapService)
    {  this.assetMapService = assetMapService; }
    public List<AssetDetail> readData() throws IOException {
        FileReader reader=new FileReader("C:\\Users\\Administrator\\Documents\\TCSINGTraining\\SpringFramework\\Asset_Tracking_Spring-01\\src\\main\\resources\\application.properties");
        Properties p = new Properties();
        p.load(reader);
        String line = "";
        List<AssetDetail> assetDetails = new ArrayList<AssetDetail>();
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(p.getProperty("CSVImportUtility.filePath")));
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
        return assetDetails;
    }
    public void assetMapping(List<AssetDetail> assetDetails){
        Set<Asset> assets = assetMapService.findAll(); //Getting all assets saved in our service.
        AtomicReference<Long> assetId = new AtomicReference<>(1L);
        assetDetails.forEach(assetDetail ->{//Going through each asset Detail and allocating it to respective asset.
            if(assets.isEmpty()){//if there are no assets existing creating a new one.
                Asset asset = Asset.builder().id(assetId.get()).assetName(assetDetail.getAssetName()).build();
                asset.getAssetDetails().add(assetDetail);//Adding assetDetail to asset.
                assetDetail.setAsset(asset);//Mapping asset to assetDetail.
                assetMapService.save(asset);//Save asset in service.
                assets.add(asset);//add assets to asset list
                assetId.getAndSet(assetId.get() + 1);//Increase AssetId
            }
            else{
                AtomicInteger flag = new AtomicInteger();//flag to check whether asset with AssetName already exists.
                assets.forEach(asset -> {
                    if (asset.getAssetName().equals(assetDetail.getAssetName())){
                        asset.getAssetDetails().add(assetDetail);//If asset with AssetName found, add detail to it.
                        assetDetail.setAsset(asset);//Mapping asset to assetDetail.
                        flag.set(1);//asset with assetName found.
                    }
                });
                if(flag.get() ==0){//asset with assetName not found. Creating a new asset object with assetName.
                    Asset asset = Asset.builder().id(assetId.get()).assetName(assetDetail.getAssetName()).build();
                    asset.getAssetDetails().add(assetDetail);
                    assets.add(asset);
                    assetDetail.setAsset(asset);
                    assetMapService.save(asset);
                    assetId.getAndSet(assetId.get() + 1);
                }
            }
        });
    }
    public List<AssetStats> assetStatCalculation(){
        List<AssetStats> listAssetStats = new ArrayList<AssetStats>();//Creating a list of Statistics for each Asset.
        Set<Asset> assets = assetMapService.findAll();
        assets.forEach(asset -> {
            listAssetStats.add(AssetStats.builder().id(asset.getId())
                    .assetName(asset.getAssetName())
                    .noOfIncidents(assetMapService.totalIncidentsOfAsset(asset.getAssetName()))
                    .totalUpTime(assetMapService.totalUptimeForAsset(asset.getAssetName()))
                    .rating(assetMapService.calculateRating(asset.getAssetName()))
                    .build());
        });
        return listAssetStats;
    }
}