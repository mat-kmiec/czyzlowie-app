package pl.czyzlowie.modules.imgw.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "imgw_hydro_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HydroData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_hydro_data_station"))
    private HydroStation station;

    @Column(name = "water_level")
    private Integer waterLevel;

    @Column(name = "water_level_date")
    private LocalDateTime waterLevelDate;

    @Column(name = "water_temperature")
    private Double waterTemperature;

    @Column(name = "water_temperature_date")
    private LocalDateTime waterTemperatureDate;

    private Double discharge;

    @Column(name = "discharge_date")
    private LocalDateTime dischargeDate;

    @Column(name = "ice_phenomenon")
    private Integer icePhenomenon;

    @Column(name = "overgrowth_phenomenon")
    private Integer overgrowthPhenomenon;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}