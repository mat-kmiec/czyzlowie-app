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
public class SpecificSpotDto extends SpotDetailsDto {
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