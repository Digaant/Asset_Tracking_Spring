package garg.digaant.Asset_Management.controllers;

import garg.digaant.Asset_Management.controllers.services.CSVImportUtility;
import garg.digaant.Asset_Management.maps.AssetMapService;
import garg.digaant.Asset_Management.models.Asset;
import garg.digaant.Asset_Management.models.AssetDetail;
import garg.digaant.Asset_Management.models.AssetStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Controller
public class AssetController {
    @Autowired
    AssetMapService assetMapService = new AssetMapService();

    CSVImportUtility csvImportUtility;

    @Value("${fileName}")
    String fileName;

    @Value("${headerKey}")
    String headerKey;

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
    private Long saveAsset(@RequestBody Asset asset) {
        Asset savedAsset = assetMapService.save(asset);
        return savedAsset.getId();
    }
    @RequestMapping("/downloadCSV")
    public void downloadCSVFile(HttpServletResponse response) throws IOException {
        csvImportUtility = new CSVImportUtility(assetMapService);
        List<AssetDetail> assetDetails = csvImportUtility.readData();
        csvImportUtility.assetMapping(assetDetails);
        List<AssetStats> listAssetStats = csvImportUtility.assetStatCalculation();
        String headerValue = String.format("attachment; filename=\"%s\"",
                this.fileName);
        response.setHeader(headerKey, headerValue);
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
