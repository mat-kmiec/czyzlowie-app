package pl.czyzlowie.modules.fish.entity;

import jakarta.persistence.*;
import lombok.*;
import pl.czyzlowie.modules.fish.entity.enums.PressureTrend;
import pl.czyzlowie.modules.fish.entity.enums.TimeOfDay;
import pl.czyzlowie.modules.fish.entity.enums.WaterLevelTrend;
import pl.czyzlowie.modules.moon.entity.Enums.MoonPhaseType;

import java.math.BigDecimal;

/**
 * Defines the technical parameters and weighting factors used by the activity forecasting
 * algorithm for a specific fish species.
 *
 * This entity acts as a configuration profile that allows the system to calculate an
 * "activity score" by comparing real-time weather, lunar, and hydrological data against
 * the species' ideal conditions.
 *
 * The parameters are organized into several logical groups:
 *
 * - Thermal Profile: Absolute activity ranges and optimal temperature windows.
 * - Atmospheric Conditions: Tolerance for air pressure levels/trends, cloud cover, and rain.
 * - Hydrological Factors: Preferred water level trends and maximum allowed water discharge.
 * - Wind Conditions: Limits for sustained wind speed and maximum gusts.
 * - Lunar and Solar: Preferred moon phases, illumination percentages, and specific times of day.
 * - Scoring Weights: Values that determine the relative importance of each factor in the
 * final calculation (e.g., prioritizing water temperature over air pressure).
 *
 * This entity is linked to FishSpecies via a one-to-one relationship and is fundamental
 * to the application's predictive capabilities.
 */
@Entity
@Table(name = "fish_algorithm_params")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FishAlgorithmParams {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "temp_min_active", precision = 4, scale = 1)
    private BigDecimal tempMinActive;

    @Column(name = "temp_max_active", precision = 4, scale = 1)
    private BigDecimal tempMaxActive;

    @Column(name = "temp_opt_min", precision = 4, scale = 1)
    private BigDecimal tempOptimalMin;

    @Column(name = "temp_opt_max", precision = 4, scale = 1)
    private BigDecimal tempOptimalMax;

    @Enumerated(EnumType.STRING)
    @Column(name = "water_level_trend")
    private WaterLevelTrend preferredWaterLevelTrend;

    @Enumerated(EnumType.STRING)
    @Column(name = "pressure_trend")
    private PressureTrend preferredPressureTrend;

    @Column(name = "pressure_min", precision = 6, scale = 1)
    private BigDecimal pressureMin;

    @Column(name = "pressure_max", precision = 6, scale = 1)
    private BigDecimal pressureMax;

    @Column(name = "wind_speed_min", precision = 4, scale = 1)
    private BigDecimal windSpeedMin;

    @Column(name = "wind_speed_max", precision = 4, scale = 1)
    private BigDecimal windSpeedMax;

    @Column(name = "cloud_cover_min")
    private Integer cloudCoverMin;

    @Column(name = "cloud_cover_max")
    private Integer cloudCoverMax;

    @Column(name = "tolerates_rain")
    private Boolean toleratesRain;

    @Enumerated(EnumType.STRING)
    @Column(name = "moon_phase")
    private MoonPhaseType preferredMoonPhase;

    @Column(name = "illumination_min", precision = 5, scale = 2)
    private BigDecimal illuminationMinPct;

    @Column(name = "illumination_max", precision = 5, scale = 2)
    private BigDecimal illuminationMaxPct;

    @Enumerated(EnumType.STRING)
    @Column(name = "time_of_day")
    private TimeOfDay preferredTimeOfDay;

    @Column(name = "wind_gust_max", precision = 4, scale = 1)
    private BigDecimal windGustMax;

    @Column(name = "discharge_max", precision = 8, scale = 3)
    private BigDecimal dischargeMax;

    @Column(name = "rain_max_mm", precision = 5, scale = 2)
    private BigDecimal rainMax;

    @Column(name = "weight_water_temp")
    private Integer weightWaterTemp;

    @Column(name = "weight_pressure")
    private Integer weightPressure;

    @Column(name = "weight_wind")
    private Integer weightWind;

    @Column(name = "weight_water_level")
    private Integer weightWaterLevel;
}