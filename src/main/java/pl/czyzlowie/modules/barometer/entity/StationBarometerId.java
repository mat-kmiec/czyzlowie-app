package pl.czyzlowie.modules.barometer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Represents a composite primary key for the StationBarometerStats entity.
 *
 * This class defines the composite key as a combination of the station ID and the station type.
 * It is used to uniquely identify records in the StationBarometerStats table.
 *
 * The station ID is a unique identifier for a weather station, while the station
 * type distinguishes between different categories of weather stations (e.g., real or virtual).
 *
 * This class is marked as embeddable to be used in the @EmbeddedId annotation
 * within the StationBarometerStats entity.
 *
 * Fields:
 * - stationId: A unique identifier for the weather station.
 * - stationType: The type of the weather station derived from the StationType enumeration.
 *
 * Implements Serializable to allow instances of this class to be serialized, as required
 * for primary keys in JPA entities.
 */
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