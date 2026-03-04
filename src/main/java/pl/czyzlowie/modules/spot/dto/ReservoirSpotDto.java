package pl.czyzlowie.modules.spot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * A data transfer object (DTO) representing detailed information about a reservoir spot.
 * This class extends the {@code SpotDetailsDto} to inherit common properties of a spot
 * and includes specific attributes relevant to reservoirs.
 *
 * The additional attributes specific to reservoir spots include:
 * - {@code areaHectares}: The surface area of the reservoir in hectares.
 * - {@code avgDepth}: The average depth of the reservoir in meters.
 * - {@code maxDepth}: The maximum depth of the reservoir in meters.
 * - {@code riverFedBy}: The name of the river(s) feeding the reservoir.
 * - {@code waterLevelFluctuations}: Information about fluctuations in the reservoir water level.
 * - {@code waterCurrent}: Details about the water current in the reservoir.
 * - {@code floodedStructures}: Information on any structures flooded to create the reservoir.
 * - {@code oldRiverBed}: Indicates the old riverbed location within the reservoir, if applicable.
 * - {@code bottomType}: The type of the reservoir's bottom (e.g., sandy, muddy, rocky).
 * - {@code dominantSpecies}: The dominant species of aquatic life in the reservoir.
 * - {@code hasPredators}: Indicates if the reservoir hosts predator species.
 * - {@code stockingInfo}: Details about stocking efforts conducted for the reservoir.
 * - {@code requiresPermit}: Indicates whether fishing requires a permit.
 * - {@code permitCostInfo}: Information about the cost of any required permits.
 * - {@code catchAndRelease}: Indicates if a catch-and-release policy is enforced.
 * - {@code silentZone}: Specifies if the reservoir includes areas designated as silent zones.
 * - {@code nightFishingRules}: Rules and regulations related to night fishing at the reservoir.
 * - {@code shoreFishing}: Information on the availability of shore fishing.
 * - {@code boatFishingAllowed}: Specifies whether boat fishing is allowed on the reservoir.
 * - {@code slipAvailability}: Information about the availability of boat slips at the reservoir.
 * - {@code accessRoad}: Details regarding access roads to the reservoir.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ReservoirSpotDto extends SpotDetailsDto {
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
