package garg.digaant.Asset_Management.models;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class AssetStats extends BasicEntity {

    //Used to write down the required Stats to the output CSV file.

    private String assetName;

    private Long noOfIncidents;

    private double totalUpTime;

    private Long rating;

    @Builder
    public AssetStats(Long id, String assetName, Long noOfIncidents, double totalUpTime, Long rating) {
        super(id);
        this.assetName = assetName;
        this.noOfIncidents = noOfIncidents;
        this.totalUpTime = totalUpTime;
        this.rating = rating;
    }
}
