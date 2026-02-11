package pl.czyzlowie.modules.moon.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MoonStationDataId implements Serializable {
    private String stationId;
    private String stationType;
    private LocalDate calculationDate;
}
