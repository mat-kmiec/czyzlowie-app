package pl.czyzlowie.modules.imgw.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import pl.czyzlowie.modules.imgw.entity.enums.PressureDirection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "imgw_meteo_trends")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeteoTrend {

    @Id
    @Column(name = "station_id")
    private String stationId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "station_id")
    private MeteoStation station;

    @Column(name = "pressure_delta_3h")
    private BigDecimal pressureDelta3h;

    @Enumerated(EnumType.STRING)
    @Column(name = "pressure_direction")
    private PressureDirection pressureDirection;

    @Column(name = "weather_stability_index")
    private Integer weatherStabilityIndex;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
