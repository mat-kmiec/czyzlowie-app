package pl.czyzlowie.modules.imgw_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.czyzlowie.modules.imgw_api.utils.ImgwDateUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "imgw_hydro_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImgwHydroData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hydro_seq_gen")
    @SequenceGenerator(name = "hydro_seq_gen", sequenceName = "imgw_hydro_data_seq", allocationSize = 50)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private ImgwHydroStation station;

    // --- STAN WODY ---
    @Column(name = "water_level")
    private Integer waterLevel;

    @Column(name = "water_level_date")
    private LocalDateTime waterLevelDate;

    // --- TEMP WODY ---
    @Column(name = "water_temperature", precision = 4, scale = 1)
    private BigDecimal waterTemperature;

    @Column(name = "water_temperature_date")
    private LocalDateTime waterTemperatureDate;

    // --- PRZEPŁYW (DISCHARGE) ---
    @Column(name = "discharge", precision = 8, scale = 3)
    private BigDecimal discharge;

    @Column(name = "discharge_date")
    private LocalDateTime dischargeDate;

    // --- ZJAWISKA ---
    @Column(name = "ice_phenomenon")
    private Integer icePhenomenon;

    @Column(name = "ice_phenomenon_date")
    private LocalDateTime icePhenomenonDate;

    @Column(name = "overgrowth_phenomenon")
    private Integer overgrowthPhenomenon;

    @Column(name = "overgrowth_phenomenon_date")
    private LocalDateTime overgrowthPhenomenonDate;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public boolean isNewerThan(ImgwHydroData other) {
        if (other == null) return true;
        return ImgwDateUtils.isDateChanged(other.getWaterLevelDate(), this.getWaterLevelDate()) ||
                ImgwDateUtils.isDateChanged(other.getDischargeDate(), this.getDischargeDate()) ||
                ImgwDateUtils.isDateChanged(other.getWaterTemperatureDate(), this.getWaterTemperatureDate()) ||
                ImgwDateUtils.isDateChanged(other.getIcePhenomenonDate(), this.getIcePhenomenonDate()) ||
                ImgwDateUtils.isDateChanged(other.getOvergrowthPhenomenonDate(), this.getOvergrowthPhenomenonDate());
    }
}