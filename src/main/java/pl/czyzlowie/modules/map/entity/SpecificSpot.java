package pl.czyzlowie.modules.map.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "map_specific_spots")
@DiscriminatorValue("SPECIFIC_SPOT")
@Getter
@Setter
public class SpecificSpot extends MapSpot {

    private String dimensionInfo;
    private String parentWaterType;

    private Double localDepth;
    private String localBottomType;
    private String localCurrent;
    private String standsCondition;

    private String effectiveMethods;
    private String bestTimeAndBaits;
    private String fishingPressure;
}
