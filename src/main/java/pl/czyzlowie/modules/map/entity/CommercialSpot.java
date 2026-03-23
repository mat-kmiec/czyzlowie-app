package pl.czyzlowie.modules.map.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a commercial spot mapped in the system. This class is a specific
 * type of map spot that includes extended attributes related to commercial
 * fishing spots.
 *
 * The CommercialSpot entity details various attributes that describe
 * the characteristics, amenities, and restrictions associated with a
 * commercial fishing location, such as profile type, reservation details,
 * facilities, and any applicable restrictions.
 *
 * This entity is mapped to the database table `map_commercials` and uses
 * a discriminator value of "COMMERCIAL" to differentiate it from other spot types.
 *
 * Attributes:
 * - profileType: Specifies the type of fishing profile associated with this spot.
 * - recordsInfo: Holds information about records or notable catches at the spot.
 * - reservationType: Indicates the reservation policy or type for this spot.
 * - pricingInfo: Describes pricing details for the spot.
 * - extraFees: Any additional fees applicable to the spot.
 * - seasonAndHours: Specifies the seasonal availability and operating hours.
 * - standsCount: The number of fishing stands available.
 * - standSizeAndDistance: Information on the size and distance of the stands.
 * - hasVipStands: Whether or not VIP stands are available.
 * - carAccessToStand: Indicates if cars can access the stands directly.
 * - hasWoodenPiers: Specifies if wooden piers are present at the spot.
 * - hasToilets: Indicates if toilets are available at the spot.
 * - hasShowers: Indicates the availability of showers at the location.
 * - hasElectricity: Specifies if there is electricity access at the spot.
 * - hasAccommodation: Indicates whether accommodation facilities are available.
 * - hasGastronomyOrShop: Whether there are gastronomy options or shops on site.
 * - allowsCampfire: Specifies if campfires are permitted at the spot.
 * - requiresCradleMat: Indicates whether a cradle mat is mandatory.
 * - requiresDisinfectant: Specifies if disinfectant is required for use.
 * - bansKeepnets: Whether keepnets are banned at this location.
 * - bansBraidedLines: Indicates if braided lines are prohibited.
 * - baitRestrictions: Describes any restrictions on the types of bait allowed.
 *
 * This class extends {@link MapSpot} as it represents a specialized type of map spot.
 *
 * Annotations:
 * - `@Entity`: Marks the class as a JPA entity.
 * - `@Table(name = "map_commercials")`: Maps the entity to the `map_commercials` table.
 * - `@DiscriminatorValue("COMMERCIAL")`: Specifies the discriminator value for this entity type.
 * - `@Getter`, `@Setter`: Enables Lombok to generate getter and setter methods for the attributes.
 */
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