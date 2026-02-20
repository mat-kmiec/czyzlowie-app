package pl.czyzlowie.modules.map.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "map_rivers")
@Getter
@Setter
public class RiverSpot extends MapSpot {

    private String riverType;
    private String channelCharacter;
    private Double avgWidth;
    private Double avgDepth;
    private String bottomType;
    private String waterStructures;


    private String dominantSpecies;
    private String fishRegion;
    private String specialSections;

    private String methodBans;
    private Boolean boatFishingAllowed;
}
