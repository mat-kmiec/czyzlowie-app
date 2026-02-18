package pl.czyzlowie.modules.moon.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents the composite identifier for Moon station data records.
 *
 * This class is used as an embedded ID in entities where data related
 * to Moon stations is stored. It defines the key components that uniquely
 * identify a data record associated with a specific station and date.
 *
 * Annotations:
 * - @Embeddable: Indicates that this class can be used as an embedded ID in an entity.
 * - Lombok annotations (@Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor, @EqualsAndHashCode)
 *   are used to generate boilerplate code such as accessors, constructors, and equality logic automatically.
 *
 * Fields:
 * - stationId: The unique identifier for a Moon station.
 * - stationType: The type or classification of the station (e.g., ground-based, orbital).
 * - calculationDate: The date for which the calculation or data is associated with the station.
 *
 * This class implements Serializable to ensure that the composite key
 * can be used in JPA and adhere to the requirements of embedded IDs.
 */
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
