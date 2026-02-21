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
public class RiverSpotDto extends SpotDetailsDto {
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
