package pl.czyzlowie.modules.map.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Represents a restriction spot derived from the abstract MapSpot class,
 * used to model areas on the map with specific restrictions or limitations.
 *
 * This entity is associated with the "map_restrictions" database table
 * and is identified by the "RESTRICTION" value in the discriminator column.
 * It provides additional characteristics related to the type of restriction,
 * the period during which the restriction is active, and the specific
 * coordinates of the restricted region.
 *
 * Attributes:
 * - restrictionType: Type of restriction applied to the spot, based on the RestrictionType enumeration.
 * - startDate: The start date when the restriction becomes effective.
 * - endDate: The end date when the restriction ceases.
 * - polygonCoordinates: Text field containing the coordinates that define the restricted area as a polygon.
 */
@Entity
@Table(name = "map_restrictions")
@DiscriminatorValue("RESTRICTION")
@Getter
@Setter
public class RestrictionSpot extends MapSpot {

    @Enumerated(EnumType.STRING)
    @Column(name = "restriction_type")
    private RestrictionType restrictionType;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(columnDefinition = "TEXT")
    private String polygonCoordinates;
}
