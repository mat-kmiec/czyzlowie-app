package pl.czyzlowie.modules.map.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Represents a specific type of map spot that corresponds to a reservoir location.
 * This entity extends the {@link MapSpot} class and includes additional attributes
 * specific to reservoirs, such as water characteristics and fishing-related details.
 *
 * This entity is mapped to the "map_reservoirs" database table, with "RESERVOIR"
 * as the discriminator value for the "spot_type" column.
 *
 * Attributes:
 * - areaHectares: The total area of the reservoir in hectares.
 * - avgDepth: The average depth of the reservoir in meters.
 * - maxDepth: The maximum depth of the reservoir in meters.
 * - riverFedBy: The name of the river feeding the reservoir.
 * - waterLevelFluctuations: Information about water level fluctuations in the reservoir.
 * - waterCurrent: Description of the water current, if any.
 * - floodedStructures: Details about any structures flooded by the reservoir.
 * - oldRiverBed: Information about the old riverbed within the reservoir.
 * - bottomType: The type of the reservoir's bottom (e.g., sandy, muddy, rocky).
 * - dominantSpecies: The dominant fish species present in the reservoir.
 * - hasPredators: Indicates whether predators are present in the reservoir.
 * - stockingInfo: Information about fish stocking in the reservoir.
 * - requiresPermit: Specifies if fishing in the reservoir requires a permit.
 * - permitCostInfo: Information about the cost of fishing permits, if applicable.
 * - catchAndRelease: Indicates whether catch-and-release policies are enforced.
 * - silentZone: Specifies if the reservoir has silent zones (no motorized activity).
 * - nightFishingRules: Rules governing night fishing in the reservoir.
 * - shoreFishing: Specifies if shore fishing is permissible and any restrictions.
 * - boatFishingAllowed: Indicates whether boat fishing is allowed in the reservoir.
 * - slipAvailability: Details about slipway availability for boat access.
 * - accessRoad: Information about access roads leading to the reservoir.
 */
@Entity
@Table(name = "map_reservoirs")
@DiscriminatorValue("RESERVOIR")
@Getter
@Setter
public class ReservoirSpot extends MapSpot {

    private Double areaHectares;
    private Double avgDepth;
    private Double maxDepth;
    private String riverFedBy;
    private String waterLevelFluctuations;
    private String waterCurrent;
    private String floodedStructures;
    private String oldRiverBed;
    private String bottomType;
    private String dominantSpecies;
    private Boolean hasPredators;
    private String stockingInfo;
    private Boolean requiresPermit;
    private String permitCostInfo;
    private Boolean catchAndRelease;
    private Boolean silentZone;
    private String nightFishingRules;
    private String shoreFishing;
    private Boolean boatFishingAllowed;
    private String slipAvailability;
    private String accessRoad;
}
