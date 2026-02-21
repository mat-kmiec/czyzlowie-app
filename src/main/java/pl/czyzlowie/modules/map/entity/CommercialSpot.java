package pl.czyzlowie.modules.map.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "map_commercials")
@DiscriminatorValue("COMMERCIAL")
@Getter
@Setter
public class CommercialSpot extends MapSpot {

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