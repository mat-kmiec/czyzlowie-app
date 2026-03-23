package pl.czyzlowie.modules.map.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a specific spot on a map with detailed characteristics.
 * This entity extends the MapSpot base class to provide additional
 * attributes relevant to a specific, localized area aimed at activities
 * such as fishing or environmental study.
 *
 * This class is mapped to the "map_specific_spots" table in the database,
 * with a discriminator value of "SPECIFIC_SPOT". SpecificSpot entities
 * contain detailed localized information, supplementing the general
 * metadata inherited from the MapSpot class.
 *
 * Attributes:
 * - `dimensionInfo`: Information about the dimensions or size of the specific spot.
 * - `parentWaterType`: The type of water body (e.g., lake, river, ocean) associated with this spot.
 * - `localDepth`: The depth of water at the specific location.
 * - `localBottomType`: The type of bottom surface (e.g., sandy, rocky) at the spot.
 * - `localCurrent`: A description of the water current in the area.
 * - `standsCondition`: The condition or state of stands or structures in the vicinity.
 * - `effectiveMethods`: Techniques or methods that are effective at this spot, e.g., fishing techniques.
 * - `bestTimeAndBaits`: Information about optimal times and baits for activities at the spot.
 * - `fishingPressure`: The level of fishing activity or pressure in the area.
 */
@Entity
@Table(name = "map_specific_spots")
@DiscriminatorValue("SPECIFIC_SPOT")
@Getter
@Setter
public class SpecificSpot extends MapSpot {

    private String dimensionInfo;
    private String parentWaterType;
    private Double localDepth;
    private String localBottomType;
    private String localCurrent;
    private String standsCondition;
    private String effectiveMethods;
    private String bestTimeAndBaits;
    private String fishingPressure;
}
