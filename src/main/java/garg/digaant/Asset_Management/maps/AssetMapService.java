package garg.digaant.Asset_Management.maps;

import garg.digaant.Asset_Management.models.Asset;
import garg.digaant.Asset_Management.models.AssetDetail;
import garg.digaant.Asset_Management.services.AssetService;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.Set;

import static java.lang.Math.round;


//Implements the AssetService interface
@Service
public class AssetMapService extends AbstractMapService<Asset, Long> implements AssetService {

    //Method to calculate the Total Incidents of a Particular Asset
    @Override
    public Long totalIncidentsOfAsset(String assetName) { /*Gets the assetName for which Incidents are to be calculated */
        Set<Asset> assets = this.findAll(); //Gets all the Assets
        Long accessed = 0L;
        for (Asset asset: assets) {
            if(asset.getAssetName().equalsIgnoreCase(assetName)){ /*Looks for entered assetName*/
                accessed = Long.valueOf(asset.getAssetDetails().size()); /*Returns Size of assetDetails(No of Incidents)*/
            }
        }
        return accessed;
    }

    @Override
    public double totalUptimeForAsset(String assetName) {
        Set<Asset> assets = this.findAll();
        double totalUpTime = 0L;//Only includes severity 2 and 3 time.(Severity 1 is down time).
        double totalSecsOfDay = 0L;// Includes time of all the severity.
        for(Asset asset : assets){
            if(asset.getAssetName().equalsIgnoreCase(assetName)){
                for (AssetDetail assetDetail : asset.getAssetDetails()) {/*For Each Asset Incident checks the severity
                    and adds accordingly*/
                    if(assetDetail.getSeverity() != 1){
                        totalUpTime += ChronoUnit.SECONDS.between(assetDetail.getEndTime(), assetDetail.getStartTime());
                    }
                    totalSecsOfDay += ChronoUnit.SECONDS.between(assetDetail.getEndTime(), assetDetail.getStartTime());
                }
            }
        }
        double upFraction = totalUpTime/totalSecsOfDay;
        return round((upFraction) * 100);// Returns UpTime as a Percentage.
    }

    @Override
    public Long calculateRating(String assetName) {
        int sev1 = 30;//Severity 1 has rating of 30
        int sev2_3 = 10;//Severity 2 and 3 of 10
        Long rating = 0L;

        Set<Asset> assets = this.findALl();
        for (Asset asset : assets) {
            if(asset.getAssetName().equalsIgnoreCase(assetName)){
                for (AssetDetail assetDetail : asset.getAssetDetails()) {/*Gets details of one Incident of the asset*/
                    if(assetDetail.getSeverity() == 1){
                        rating += sev1;
                    }else if(assetDetail.getSeverity() == 2 || assetDetail.getSeverity() == 3){
                        rating += sev2_3;
                    }
                }
            }
        }
        return rating;
    }

    @Override
    public Set<Asset> findAll() {
        return super.findALl();
    }

    @Override
    public Asset findById(Long id) {
        return super.findById(id);
    }

    @Override
    public Asset save(Asset object) {
        return super.save(object);
    }

    @Override
    public void delete(Asset object) {
        super.delete(object);
    }

    @Override
    public void deleteById(Long id) {
        super.deleteById(id);
    }
}
