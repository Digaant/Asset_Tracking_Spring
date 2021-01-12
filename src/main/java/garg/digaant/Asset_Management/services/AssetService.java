package garg.digaant.Asset_Management.services;

import garg.digaant.Asset_Management.models.Asset;


public interface AssetService extends CrudService<Asset, Long>{
    //Basic CRUD Services as well as the required functions to calculate Stats(NoOfIncidents, UpTime as %,Rating)

    Long totalIncidentsOfAsset(String assetName);
    double totalUptimeForAsset(String assetName);
    Long calculateRating(String assetName);
}
