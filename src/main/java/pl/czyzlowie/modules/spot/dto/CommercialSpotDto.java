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
public class CommercialSpotDto extends SpotDetailsDto {
    private String profileType;
    private String recordsInfo;
    private String reservationType;
    private String pricingInfo;
    private String extraFees;
    private String seasonAndHours;
    private Integer standsCount;
    private String standSizeAndDistance;
    private Boolean hasVipStands;
    private Boolean carAccessToStand;
    private Boolean hasWoodenPiers;
    private Boolean hasToilets;
    private Boolean hasShowers;
    private Boolean hasElectricity;
    private Boolean hasAccommodation;
    private Boolean hasGastronomyOrShop;
    private Boolean allowsCampfire;
    private Boolean requiresCradleMat;
    private Boolean requiresDisinfectant;
    private Boolean bansKeepnets;
    private Boolean bansBraidedLines;
    private String baitRestrictions;
}
