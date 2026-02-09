package pl.czyzlowie.modules.forecast.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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