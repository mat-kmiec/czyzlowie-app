package pl.czyzlowie.modules.moon.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "moon_station_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoonStationData {

    @EmbeddedId
    private MoonStationDataId id;

    private LocalDateTime moonrise;

    private LocalDateTime moonset;

    private LocalDateTime transit;

    private LocalDateTime sunrise;

    private LocalDateTime sunset;

    @Column(name = "day_length_sec")
    private Long dayLengthSec;

    @Column(name = "max_altitude", precision = 4, scale = 2)
    private BigDecimal maxAltitude;
}
