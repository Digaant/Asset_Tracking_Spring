package garg.digaant.Asset_Management.models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "asset_details")
public class AssetDetail extends BasicEntity{
    //Contains details for one Incident of Asset(AssetName, StartTime,EndTime,Severity)

    @ManyToOne
    private Asset asset;

    @Column
    private String assetName;

    @Column
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    @Column
    private int severity;

    @Builder
    public AssetDetail(Long id, String assetName, LocalDateTime startTime, LocalDateTime endTime, int severity){
        super(id);
        this.assetName = assetName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.severity = severity;
    }

}
