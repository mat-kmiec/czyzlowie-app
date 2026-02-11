package pl.czyzlowie.modules.forecast.entity;

import jakarta.persistence.*;
import lombok.*;
import pl.czyzlowie.modules.forecast.entity.VirtualStation;
import pl.czyzlowie.modules.imgw.entity.ImgwSynopStation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a weather forecast entity that stores meteorological data
 * for a specific location and time. This class is mapped to the "weather_forecast"
 * table in the database and contains various attributes describing the forecasted
 * weather conditions.
 *
 * Key Features:
 * - Stores the ID of the forecast, forecasted time, and the time the data was fetched.
 * - Links to a synoptic station or a virtual station providing the forecast data.
 * - Includes various weather attributes such as temperature, apparent temperature,
 *   pressure, wind speed, gusts, direction, rain, cloud cover, and weather codes.
 * - Contains information about UV index, sunrise and sunset times, and maximum UV index.
 *
 * Relationships:
 * - Links to an {@code ImgwSynopStation} entity, which represents a physical synoptic station.
 * - Links to a {@code VirtualStation} entity, which represents a virtual weather station.
 *
 * Utility Methods:
 * - `isVirtual()`: Determines if the forecast is associated with a virtual station.
 */
@Entity
@Table(name = "weather_forecast")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherForecast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "forecast_time", nullable = false)
    private LocalDateTime forecastTime;

    @Column(name = "fetched_at")
    @Builder.Default
    private LocalDateTime fetchedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "synop_station_id")
    private ImgwSynopStation synopStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "virtual_station_id")
    private VirtualStation virtualStation;

    @Column(name = "temp_c")
    private BigDecimal temperature;

    @Column(name = "apparent_temp_c")
    private BigDecimal apparentTemperature;

    @Column(name = "pressure_hpa")
    private BigDecimal pressure;

    @Column(name = "wind_speed_kmh")
    private BigDecimal windSpeed;

    @Column(name = "wind_gusts_kmh")
    private BigDecimal windGusts;

    @Column(name = "wind_dir_deg")
    private Integer windDirection;

    @Column(name = "rain_mm")
    private BigDecimal rain;

    @Column(name = "cloud_cover_pct")
    private Integer cloudCover;

    @Column(name = "weather_code")
    private Integer weatherCode;

    @Column(name = "uv_index")
    private BigDecimal uvIndex;

    @Column(name = "sunrise")
    private LocalDateTime sunrise;

    @Column(name = "sunset")
    private LocalDateTime sunset;

    @Column(name = "uv_index_max")
    private BigDecimal uvIndexMax;

    public boolean isVirtual() {
        return virtualStation != null;
    }
}