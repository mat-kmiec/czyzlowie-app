package pl.czyzlowie.modules.map.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


/**
 * Represents a specific type of map spot related to a lake.
 * Extends the MapSpot class and provides additional attributes
 * specific to lake features and characteristics.
 *
 * This entity is mapped to the "map_lakes" database table and uses
 * a discriminator value of "LAKE" to differentiate it from other map spots.
 */
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
