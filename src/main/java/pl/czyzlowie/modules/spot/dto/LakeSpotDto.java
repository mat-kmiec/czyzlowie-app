package pl.czyzlowie.modules.spot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * A data transfer object (DTO) representing detailed information about a lake spot.
 * This class extends the {@code SpotDetailsDto} to inherit common properties of a spot
 * and includes specific details relevant to lakes.
 *
 * The additional attributes specific to lake spots include:
 * - {@code lakeType}: The type of lake (e.g., natural, artificial).
 * - {@code areaHectares}: The area of the lake in hectares.
 * - {@code avgDepth}: The average depth of the lake in meters.
 * - {@code maxDepth}: The maximum depth of the lake in meters.
 * - {@code bottomFormation}: The geological formation of the lake's bottom.
 * - {@code bottomType}: The type of the lake's bottom (e.g., sandy, rocky).
 * - {@code waterClarity}: The clarity or transparency of the lake's water.
 * - {@code vegetation}: Information about vegetation in and around the lake.
 * - {@code dominantSpecies}: The species of fish or other aquatic life dominant in the lake.
 * - {@code hasPredators}: Indicates whether the lake has predator species.
 * - {@code stockingInfo}: Details of any stocking programs conducted for the lake.
 * - {@code requiresPermit}: Indicates whether fishing requires a permit.
 * - {@code permitCostInfo}: Information about the cost of the fishing permit.
 * - {@code catchAndRelease}: Indicates if a catch-and-release policy is enforced.
 * - {@code silentZone}: Specifies if the lake has a silent zone restriction.
 * - {@code shoreFishing}: Information on the availability of shore fishing.
 * - {@code hasPiers}: Indicates whether the lake has piers for fishing or access.
 * - {@code boatFishingAllowed}: Specifies if boat fishing is allowed on the lake.
 * - {@code accessRoad}: Details about the access road to the lake.
 * - {@code hasParking}: Indicates whether parking facilities are available near the lake.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LakeSpotDto extends SpotDetailsDto {
    private String lakeType;
    private Double areaHectares;
    private Double avgDepth;
    private Double maxDepth;
    private String bottomFormation;
    private String bottomType;
    private String waterClarity;
    private String vegetation;
    private String dominantSpecies;
    private Boolean hasPredators;
    private String stockingInfo;
    private Boolean requiresPermit;
    private String permitCostInfo;
    private Boolean catchAndRelease;
    private Boolean silentZone;
    private String shoreFishing;
    private Boolean hasPiers;
    private Boolean boatFishingAllowed;
    private String accessRoad;
    private Boolean hasParking;
}
