package pl.czyzlowie.modules.map.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "map_reservoirs")
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
