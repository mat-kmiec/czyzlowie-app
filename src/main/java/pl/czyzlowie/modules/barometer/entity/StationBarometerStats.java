package pl.czyzlowie.modules.barometer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import pl.czyzlowie.modules.barometer.converter.BarometerChartDataConverter;
import pl.czyzlowie.modules.barometer.dto.BarometerChartData;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "station_barometer_stats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StationBarometerStats {

    @EmbeddedId
    private StationBarometerId id;

    @Column(name = "current_pressure", precision = 6, scale = 1)
    private BigDecimal currentPressure;

    @Enumerated(EnumType.STRING)
    @Column(name = "trend_24h", length = 20)
    private PressureTrend trend24h;

    @Column(name = "delta_24h", precision = 5, scale = 2)
    private BigDecimal delta24h;

    @Column(name = "delta_3d", precision = 5, scale = 2)
    private BigDecimal delta3d;

    @Column(name = "delta_5d", precision = 5, scale = 2)
    private BigDecimal delta5d;

    @Convert(converter = BarometerChartDataConverter.class)
    @Column(name = "barometer_chart_json", columnDefinition = "TEXT", nullable = false)
    private BarometerChartData chartData;

    @Column(name = "pressure_stability_index")
    private Integer pressureStabilityIndex;

    @Column(name = "front_approaching")
    private Boolean frontApproaching;

    @UpdateTimestamp
    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;
}