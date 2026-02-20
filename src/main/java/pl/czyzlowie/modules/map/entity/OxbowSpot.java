package pl.czyzlowie.modules.map.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "map_oxbows")
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
