package garg.digaant.Asset_Management.models;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Entity

public class Asset extends BasicEntity{

    @Column
    private String assetName;

    /*Contains all the input details, including all incidences of the asset,
        each Incident is an AssetDetail Class Object.
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "asset")
    private Set<AssetDetail> assetDetails = new HashSet<>();

    @Builder
    public Asset(Long id, String assetName) {
        super(id);
        this.assetName = assetName;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public Set<AssetDetail> getAssetDetails() {
        return assetDetails;
    }

    public void setAssetDetails(Set<AssetDetail> assetDetails) {
        this.assetDetails = assetDetails;
    }
}
