package pl.czyzlowie.modules.map.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "map_lakes")
@DiscriminatorValue("LAKE")
@Getter
@Setter
public class LakeSpot extends MapSpot {

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
