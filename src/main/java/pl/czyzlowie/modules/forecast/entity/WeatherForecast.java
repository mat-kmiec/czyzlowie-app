package pl.czyzlowie.modules.forecast.entity;

import jakarta.persistence.*;
import lombok.*;
import pl.czyzlowie.modules.forecast.entity.VirtualStation;
import pl.czyzlowie.modules.imgw.entity.ImgwSynopStation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    public boolean isVirtual() {
        return virtualStation != null;
    }
}