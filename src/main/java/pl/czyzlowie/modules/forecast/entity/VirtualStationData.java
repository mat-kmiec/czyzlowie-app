package pl.czyzlowie.modules.forecast.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents meteorological data associated with a virtual weather station.
 * This class is mapped to the table "virtual_station_data" in the database.
 * It contains various measurements and details about weather conditions
 * captured at specific points in time for a given virtual station.
 *
 * Fields:
 * - id: The unique identifier for a recorded weather data entry.
 * - virtualStation: The associated virtual weather station for which the data is recorded.
 * - measurementTime: The time at which the measurement was taken.
 * - fetchedAt: The time when the data was retrieved. Defaults to the current time.
 * - temperature: The recorded air temperature in degrees Celsius.
 * - pressure: The recorded atmospheric pressure in hectopascals.
 * - windSpeed: The recorded wind speed in kilometers per hour.
 * - windGusts: The recorded wind gust speed in kilometers per hour.
 * - windDirection: The recorded wind direction in degrees.
 * - rain: The recorded rainfall amount in millimeters.
 * - humidity: The recorded relative humidity percentage.
 * - weatherCode: The weather condition code corresponding to the weather state.
 * - apparentTemperature: The perceived air temperature in degrees Celsius.
 */
@Entity
@Table(name = "virtual_station_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VirtualStationData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "virtual_station_id", nullable = false)
    private VirtualStation virtualStation;

    @Column(name = "measurement_time", nullable = false)
    private LocalDateTime measurementTime;

    @Column(name = "fetched_at")
    @Builder.Default
    private LocalDateTime fetchedAt = LocalDateTime.now();

    @Column(name = "temp_c")
    private BigDecimal temperature;

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

    @Column(name = "humidity_pct")
    private BigDecimal humidity;

    @Column(name = "weather_code")
    private Integer weatherCode;

    @Column(name = "apparent_temp_c")
    private BigDecimal apparentTemperature;
}