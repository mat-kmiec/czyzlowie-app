package pl.czyzlowie.modules.spot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * A data transfer object (DTO) representing detailed information about a commercial fishing spot.
 * This class extends the SpotDetailsDto to inherit common spot details and includes
 * specific attributes relevant to commercial spots.
 *
 * The additional attributes specific to commercial spots include:
 * - profileType: The type of fishing profile associated with the spot.
 * - recordsInfo: Information about records or achievements related to the spot.
 * - reservationType: The type of reservation system in place for the spot.
 * - pricingInfo: Detailed pricing information for accessing or using the spot.
 * - extraFees: Information on additional fees that might apply.
 * - seasonAndHours: Seasonal availability and operating hours of the spot.
 * - standsCount: The number of fishing stands available at the spot.
 * - standSizeAndDistance: Details of the size and distance of each stand.
 * - hasVipStands: Indicates if the spot includes VIP fishing stands.
 * - carAccessToStand: Indicates if the stands are accessible by car.
 * - hasWoodenPiers: Indicates if the spot has wooden piers.
 * - hasToilets: Indicates if toilet facilities are available at the spot.
 * - hasShowers: Indicates if shower facilities are available at the spot.
 * - hasElectricity: Indicates if electricity is available at the spot.
 * - hasAccommodation: Indicates if accommodation is available at or near the spot.
 * - hasGastronomyOrShop: Indicates if the spot has gastronomy or shops nearby.
 * - allowsCampfire: Indicates if campfires are allowed at the spot.
 * - requiresCradleMat: Specifies if the use of cradle mats is required.
 * - requiresDisinfectant: Specifies if the use of disinfectants is required.
 * - bansKeepnets: Indicates if the use of keepnets is prohibited.
 * - bansBraidedLines: Indicates if the use of braided lines is prohibited.
 * - baitRestrictions: Details regarding any bait restrictions at the spot.
 */
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
