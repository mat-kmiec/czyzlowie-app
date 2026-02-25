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
@Table(name = "imgw_meteo_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImgwMeteoData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hydro_seq_gen")
    @SequenceGenerator(name = "hydro_seq_gen", sequenceName = "imgw_hydro_data_seq", allocationSize = 50)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private ImgwMeteoStation station;

    // --- TEMPERATURY ---
    @Column(name = "air_temp", precision = 4, scale = 1)
    private BigDecimal airTemp;

    @Column(name = "air_temp_time")
    private LocalDateTime airTempTime;

    @Column(name = "ground_temp", precision = 4, scale = 1)
    private BigDecimal groundTemp;

    @Column(name = "ground_temp_time")
    private LocalDateTime groundTempTime;

    // --- WIATR (GŁÓWNY) ---
    @Column(name = "wind_direction")
    private Integer windDirection;

    @Column(name = "wind_avg_speed", precision = 4, scale = 1)
    private BigDecimal windAvgSpeed;

    @Column(name = "wind_max_speed", precision = 4, scale = 1)
    private BigDecimal windMaxSpeed;

    @Column(name = "wind_measurement_time")
    private LocalDateTime windMeasurementTime;

    // --- WIATR (PORYW 10MIN) ---
    @Column(name = "wind_gust_10min", precision = 4, scale = 1)
    private BigDecimal windGust10min;

    @Column(name = "wind_gust_10min_time")
    private LocalDateTime windGust10minTime;

    // --- WILGOTNOŚĆ ---
    @Column(name = "relative_humidity", precision = 4, scale = 1)
    private BigDecimal relativeHumidity;

    @Column(name = "relative_humidity_time")
    private LocalDateTime relativeHumidityTime;

    // --- OPAD ---
    @Column(name = "precipitation_10min", precision = 5, scale = 2)
    private BigDecimal precipitation10min;

    @Column(name = "precipitation_10min_time")
    private LocalDateTime precipitation10minTime;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public boolean isNewerThan(ImgwMeteoData other) {
        if (other == null) return true;
        return ImgwDateUtils.isDateChanged(other.getAirTempTime(), this.getAirTempTime()) ||
                ImgwDateUtils.isDateChanged(other.getWindMeasurementTime(), this.getWindMeasurementTime()) ||
                ImgwDateUtils.isDateChanged(other.getPrecipitation10minTime(), this.getPrecipitation10minTime()) ||
                ImgwDateUtils.isDateChanged(other.getWindGust10minTime(), this.getWindGust10minTime());
    }
}