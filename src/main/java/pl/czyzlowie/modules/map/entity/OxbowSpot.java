package pl.czyzlowie.modules.map.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an oxbow spot, a specialized type of geographical location derived from the MapSpot base class.
 * This class provides additional attributes and characteristics specific to oxbow lakes or similar water bodies.
 *
 * This entity is mapped to the "map_oxbows" table in the database, with a discriminator value of "OXBOW".
 * It includes various properties describing the environmental and physical aspects of the oxbow, as well as
 * its accessibility and suitability for recreational or ecological purposes.
 *
 * Attributes:
 * - `areaHectares`: The surface area of the oxbow in hectares.
 * - `avgDepth`: The average depth of the oxbow in meters.
 * - `maxDepth`: The maximum depth of the oxbow in meters.
 * - `riverConnection`: Describes how the oxbow is connected to a river (e.g., permanently, seasonally, or not at all).
 * - `siltingLevel`: The level of sediment accumulation in the oxbow.
 * - `overgrowthLevel`: The extent of vegetation cover within or around the oxbow.
 * - `oxygenDepletionRisk`: Indicates whether the oxbow is at risk of oxygen depletion.
 * - `driesUp`: Specifies if the oxbow dries up during certain times of the year.
 * - `dominantFish`: The dominant fish species or types typically found in the oxbow.
 * - `shoreAccess`: Describes the accessibility of the oxbow's shore (e.g., open, restricted, or difficult to access).
 * - `wadersRequired`: Indicates whether waders are necessary to access parts of the oxbow.
 * - `snagsLevel`: The density or presence of snags (submerged or partially submerged debris) in the oxbow.
 * - `bestSeasons`: The recommended seasons for visiting or utilizing the oxbow for recreational or ecological activities.
 */
@Entity
@Table(name = "map_oxbows")
@DiscriminatorValue("OXBOW")
@Getter
@Setter
public class OxbowSpot extends MapSpot {

    private Double areaHectares;
    private Double avgDepth;
    private Double maxDepth;

    private String riverConnection;
    private String siltingLevel;
    private String overgrowthLevel;
    private Boolean oxygenDepletionRisk;
    private Boolean driesUp;

    private String dominantFish;

    private String shoreAccess;
    private Boolean wadersRequired;
    private String snagsLevel;
    private String bestSeasons;
}
