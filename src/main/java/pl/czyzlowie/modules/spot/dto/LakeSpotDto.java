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
