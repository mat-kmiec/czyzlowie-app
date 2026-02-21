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
public class OxbowSpotDto extends SpotDetailsDto {
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