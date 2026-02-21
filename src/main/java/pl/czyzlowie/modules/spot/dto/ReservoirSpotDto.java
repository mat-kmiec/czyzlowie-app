package pl.czyzlowie.modules.spot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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
