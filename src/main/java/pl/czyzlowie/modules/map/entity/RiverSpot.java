package pl.czyzlowie.modules.map.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Represents a river-specific geographical location or point of interest,
 * extending the base MapSpot class. This entity is used to model information about rivers,
 * including their characteristics, fishing regulations, and ecological details.
 *
 * This class is mapped to the "map_rivers" database table, with a discriminator
 * value of "RIVER". It includes various attributes defining the physical, ecological,
 * and fishing-related properties of a river spot.
 *
 * Attributes:
 * - `riverType`: The type of the river (e.g., mountain river, lowland river, etc.).
 * - `channelCharacter`: The characteristics of the river channel (e.g., meandering, braided).
 * - `avgWidth`: The average width of the river, measured in meters.
 * - `avgDepth`: The average depth of the river, measured in meters.
 * - `bottomType`: Description of the riverbed or bottom type (e.g., sandy, rocky).
 * - `waterStructures`: Description of any structures present in the river (e.g., weirs, dams).
 * - `dominantSpecies`: The dominant fish species found in this river.
 * - `fishRegion`: The fishing region or ecological category associated with this river.
 * - `specialSections`: Describes specific sections of the river with unique characteristics or restrictions.
 * - `methodBans`: Specifies prohibited fishing methods or techniques applicable in this river.
 * - `boatFishingAllowed`: Indicates whether fishing from a boat is allowed in this river spot.
 */
@Entity
@Table(name = "map_rivers")
@DiscriminatorValue("RIVER")
@Getter
@Setter
public class RiverSpot extends MapSpot {

    private String riverType;
    private String channelCharacter;
    private Double avgWidth;
    private Double avgDepth;
    private String bottomType;
    private String waterStructures;
    private String dominantSpecies;
    private String fishRegion;
    private String specialSections;
    private String methodBans;
    private Boolean boatFishingAllowed;
}
