package pl.czyzlowie.modules.imgw.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import pl.czyzlowie.modules.imgw.entity.enums.WaterDirection;

import java.time.LocalDateTime;

@Entity
@Table(name = "imgw_hydro_trends")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HydroTrend {

    @Id
    @Column(name = "station_id")
    private String stationId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "station_id", foreignKey = @ForeignKey(name = "fk_hydro_trend_station"))
    private HydroStation station;

    @Column(name = "water_level_delta_3h")
    private Integer waterLevelDelta3h;

    @Column(name = "water_level_delta_24h")
    private Integer waterLevelDelta24h;

    @Enumerated(EnumType.STRING)
    @Column(name = "water_direction")
    private WaterDirection waterDirection;

    @Column(name = "water_temp_delta_12h")
    private Double waterTempDelta12h;

    @Column(name = "is_flooding_risk")
    private Boolean isFloodingRisk = false;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}