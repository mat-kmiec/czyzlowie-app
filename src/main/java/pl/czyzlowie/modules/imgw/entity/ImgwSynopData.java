package pl.czyzlowie.modules.imgw.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "imgw_synop_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImgwSynopData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private ImgwSynopStation station;

    @Column(name = "measurement_date")
    private LocalDate measurementDate;

    @Column(name = "measurement_hour")
    private Integer measurementHour;

    @Column(precision = 4, scale = 1)
    private BigDecimal temperature;

    @Column(name = "wind_speed")
    private Integer windSpeed;

    @Column(name = "wind_direction")
    private Integer windDirection;

    @Column(name = "relative_humidity", precision = 4, scale = 1)
    private BigDecimal relativeHumidity;

    @Column(name = "total_precipitation", precision = 5, scale = 2)
    private BigDecimal totalPrecipitation;

    @Column(precision = 6, scale = 1)
    private BigDecimal pressure;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}