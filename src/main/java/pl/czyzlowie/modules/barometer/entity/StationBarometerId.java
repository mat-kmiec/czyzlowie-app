package pl.czyzlowie.modules.barometer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StationBarometerId implements Serializable {

    @Column(name = "station_id", length = 20, nullable = false)
    private String stationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "station_type", length = 20, nullable = false)
    private StationType stationType;
}