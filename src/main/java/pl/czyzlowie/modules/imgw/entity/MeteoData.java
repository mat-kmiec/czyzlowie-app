package pl.czyzlowie.modules.imgw.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "imgw_meteo_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeteoData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_meteo_data_station"))
    private MeteoStation station;

    @Column(name = "measurement_date")
    private LocalDateTime measurementDate;

    @Column(name = "measurement_hour")
    private Integer measurementHour;

    private Double temperature;

    @Column(name = "wind_speed")
    private Double windSpeed;

    @Column(name = "wind_direction")
    private Integer windDirection;

    @Column(name = "relative_humidity")
    private Double relativeHumidity;

    @Column(name = "total_precipitation")
    private Double totalPrecipitation;

    private Double pressure;

    @Column(name = "observed_at")
    private LocalDateTime observedAt;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}