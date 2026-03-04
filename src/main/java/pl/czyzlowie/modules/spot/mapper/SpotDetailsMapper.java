package pl.czyzlowie.modules.spot.mapper;


import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.map.entity.*;
import pl.czyzlowie.modules.spot.dto.*;

/**
 * SpotDetailsMapper is responsible for converting instances of various
 * MapSpot implementations into their corresponding SpotDetailsDto representations.
 * It ensures type-specific properties are appropriately mapped while handling
 * all supported spot types, such as LakeSpot, RiverSpot, CommercialSpot, etc.
 * Unsupported spot types result in an exception.
 */
@Component
public class SpotDetailsMapper {

    /**
     * Converts a given MapSpot instance into a SpotDetailsDto instance.
     *
     * @param spot the MapSpot instance to be converted; can be of various specific types such as
     *             LakeSpot, RiverSpot, CommercialSpot, ReservoirSpot, OxbowSpot, BoatSlip, or SpecificSpot.
     *             If the spot is null or an instance of RestrictionSpot, the method returns null.
     * @return a SpotDetailsDto representation of the provided MapSpot. Returns null if the MapSpot is
     *         null or an instance of RestrictionSpot. Throws IllegalArgumentException for unsupported
     *         MapSpot types.
     */
    public SpotDetailsDto mapToDto(MapSpot spot) {
        if (spot == null || spot instanceof RestrictionSpot) {
            return null;
        }

        SpotDetailsDto dto;

        if (spot instanceof LakeSpot lake) {
            dto = LakeSpotDto.builder()
                    .lakeType(lake.getLakeType())
                    .areaHectares(lake.getAreaHectares())
                    .avgDepth(lake.getAvgDepth())
                    .maxDepth(lake.getMaxDepth())
                    .bottomFormation(lake.getBottomFormation())
                    .bottomType(lake.getBottomType())
                    .waterClarity(lake.getWaterClarity())
                    .vegetation(lake.getVegetation())
                    .dominantSpecies(lake.getDominantSpecies())
                    .hasPredators(lake.getHasPredators())
                    .stockingInfo(lake.getStockingInfo())
                    .requiresPermit(lake.getRequiresPermit())
                    .permitCostInfo(lake.getPermitCostInfo())
                    .catchAndRelease(lake.getCatchAndRelease())
                    .silentZone(lake.getSilentZone())
                    .shoreFishing(lake.getShoreFishing())
                    .hasPiers(lake.getHasPiers())
                    .boatFishingAllowed(lake.getBoatFishingAllowed())
                    .accessRoad(lake.getAccessRoad())
                    .hasParking(lake.getHasParking())
                    .build();
        } else if (spot instanceof RiverSpot river) {
            dto = RiverSpotDto.builder()
                    .riverType(river.getRiverType())
                    .channelCharacter(river.getChannelCharacter())
                    .avgWidth(river.getAvgWidth())
                    .avgDepth(river.getAvgDepth())
                    .bottomType(river.getBottomType())
                    .waterStructures(river.getWaterStructures())
                    .dominantSpecies(river.getDominantSpecies())
                    .fishRegion(river.getFishRegion())
                    .specialSections(river.getSpecialSections())
                    .methodBans(river.getMethodBans())
                    .boatFishingAllowed(river.getBoatFishingAllowed())
                    .build();
        } else if (spot instanceof CommercialSpot com) {
            dto = CommercialSpotDto.builder()
                    .profileType(com.getProfileType())
                    .recordsInfo(com.getRecordsInfo())
                    .reservationType(com.getReservationType())
                    .pricingInfo(com.getPricingInfo())
                    .extraFees(com.getExtraFees())
                    .seasonAndHours(com.getSeasonAndHours())
                    .standsCount(com.getStandsCount())
                    .standSizeAndDistance(com.getStandSizeAndDistance())
                    .hasVipStands(com.getHasVipStands())
                    .carAccessToStand(com.getCarAccessToStand())
                    .hasWoodenPiers(com.getHasWoodenPiers())
                    .hasToilets(com.getHasToilets())
                    .hasShowers(com.getHasShowers())
                    .hasElectricity(com.getHasElectricity())
                    .hasAccommodation(com.getHasAccommodation())
                    .hasGastronomyOrShop(com.getHasGastronomyOrShop())
                    .allowsCampfire(com.getAllowsCampfire())
                    .requiresCradleMat(com.getRequiresCradleMat())
                    .requiresDisinfectant(com.getRequiresDisinfectant())
                    .bansKeepnets(com.getBansKeepnets())
                    .bansBraidedLines(com.getBansBraidedLines())
                    .baitRestrictions(com.getBaitRestrictions())
                    .build();
        } else if (spot instanceof ReservoirSpot res) {
            dto = ReservoirSpotDto.builder()
                    .areaHectares(res.getAreaHectares())
                    .avgDepth(res.getAvgDepth())
                    .maxDepth(res.getMaxDepth())
                    .riverFedBy(res.getRiverFedBy())
                    .waterLevelFluctuations(res.getWaterLevelFluctuations())
                    .waterCurrent(res.getWaterCurrent())
                    .floodedStructures(res.getFloodedStructures())
                    .oldRiverBed(res.getOldRiverBed())
                    .bottomType(res.getBottomType())
                    .dominantSpecies(res.getDominantSpecies())
                    .hasPredators(res.getHasPredators())
                    .stockingInfo(res.getStockingInfo())
                    .requiresPermit(res.getRequiresPermit())
                    .permitCostInfo(res.getPermitCostInfo())
                    .catchAndRelease(res.getCatchAndRelease())
                    .silentZone(res.getSilentZone())
                    .nightFishingRules(res.getNightFishingRules())
                    .shoreFishing(res.getShoreFishing())
                    .boatFishingAllowed(res.getBoatFishingAllowed())
                    .slipAvailability(res.getSlipAvailability())
                    .accessRoad(res.getAccessRoad())
                    .build();
        } else if (spot instanceof OxbowSpot oxb) {
            dto = OxbowSpotDto.builder()
                    .areaHectares(oxb.getAreaHectares())
                    .avgDepth(oxb.getAvgDepth())
                    .maxDepth(oxb.getMaxDepth())
                    .riverConnection(oxb.getRiverConnection())
                    .siltingLevel(oxb.getSiltingLevel())
                    .overgrowthLevel(oxb.getOvergrowthLevel())
                    .oxygenDepletionRisk(oxb.getOxygenDepletionRisk())
                    .driesUp(oxb.getDriesUp())
                    .dominantFish(oxb.getDominantFish())
                    .shoreAccess(oxb.getShoreAccess())
                    .wadersRequired(oxb.getWadersRequired())
                    .snagsLevel(oxb.getSnagsLevel())
                    .bestSeasons(oxb.getBestSeasons())
                    .build();
        } else if (spot instanceof BoatSlip slip) {
            dto = BoatSlipDto.builder()
                    .status(slip.getStatus())
                    .accessType(slip.getAccessType())
                    .feeInfo(slip.getFeeInfo())
                    .openingHours(slip.getOpeningHours())
                    .surfaceType(slip.getSurfaceType())
                    .incline(slip.getIncline())
                    .unitLimit(slip.getUnitLimit())
                    .endDepth(slip.getEndDepth())
                    .trailerParking(slip.getTrailerParking())
                    .hasMooringPier(slip.getHasMooringPier())
                    .lightingAndMonitoring(slip.getLightingAndMonitoring())
                    .maneuveringSpace(slip.getManeuveringSpace())
                    .navigationalAlerts(slip.getNavigationalAlerts())
                    .build();
        } else if (spot instanceof SpecificSpot spec) {
            dto = SpecificSpotDto.builder()
                    .dimensionInfo(spec.getDimensionInfo())
                    .parentWaterType(spec.getParentWaterType())
                    .localDepth(spec.getLocalDepth())
                    .localBottomType(spec.getLocalBottomType())
                    .localCurrent(spec.getLocalCurrent())
                    .standsCondition(spec.getStandsCondition())
                    .effectiveMethods(spec.getEffectiveMethods())
                    .bestTimeAndBaits(spec.getBestTimeAndBaits())
                    .fishingPressure(spec.getFishingPressure())
                    .build();
        } else {
            throw new IllegalArgumentException("Nieobsługiwany typ punktu mapy: " + spot.getClass().getSimpleName());
        }

        return populateCommonFields(dto, spot);
    }

    /**
     * Populates common fields in a SpotDetailsDto object using data from a MapSpot entity.
     *
     * @param dto the SpotDetailsDto object to be populated
     * @param spot the MapSpot entity containing the source data
     * @return the updated SpotDetailsDto object with populated fields
     */
    private SpotDetailsDto populateCommonFields(SpotDetailsDto dto, MapSpot spot) {
        dto.setId(spot.getId());
        dto.setName(spot.getName());
        dto.setSlug(spot.getSlug());
        dto.setSpotType(spot.getSpotType());
        dto.setLatitude(spot.getLatitude());
        dto.setLongitude(spot.getLongitude());
        dto.setProvince(spot.getProvince());
        dto.setNearestCity(spot.getNearestCity());
        dto.setDescription(spot.getDescription());
        dto.setManager(spot.getManager());
        return dto;
    }
}