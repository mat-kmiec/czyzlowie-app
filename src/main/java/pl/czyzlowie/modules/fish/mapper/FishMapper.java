package pl.czyzlowie.modules.fish.mapper;

import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.fish.dto.FishDetailsDto;
import pl.czyzlowie.modules.fish.dto.FishListElementDto;
import pl.czyzlowie.modules.fish.entity.FishSpecies;

@Component
public class FishMapper {

    public FishListElementDto toListDto(FishSpecies fish) {
        if (fish == null) return null;

        return FishListElementDto.builder()
                .id(fish.getId())
                .name(fish.getName())
                .latinName(fish.getLatinName())
                .slug(fish.getSlug())
                .imgUrl(fish.getImgUrl())
                .category(fish.getCategory())
                .build();
    }

    public FishDetailsDto toDetailsDto(FishSpecies entity) {
        if (entity == null) return null;

        FishDetailsDto.FishDetailsDtoBuilder dto = FishDetailsDto.builder()
                .name(entity.getName())
                .latinName(entity.getLatinName())
                .englishName(entity.getEnglishName())
                .slug(entity.getSlug())
                .imgUrl(entity.getImgUrl())
                .category(entity.getCategory())
                .description(entity.getDescription());

        if (entity.getPzwRegulations() != null) {
            dto.pzwRegulations(FishDetailsDto.PzwDto.builder()
                    .minDimension(entity.getPzwRegulations().getMinDimension())
                    .maxDimension(entity.getPzwRegulations().getMaxDimension())
                    .dimensionExceptions(entity.getPzwRegulations().getDimensionExceptions())
                    .protectionPeriod(entity.getPzwRegulations().getProtectionPeriod())
                    .spawningSeason(entity.getPzwRegulations().getSpawningSeason())
                    .dailyLimitPieces(entity.getPzwRegulations().getDailyLimitPieces())
                    .dailyLimitWeight(entity.getPzwRegulations().getDailyLimitWeight())
                    .sharedLimitGroup(entity.getPzwRegulations().getSharedLimitGroup())
                    .additionalRules(entity.getPzwRegulations().getAdditionalRules())
                    .build());
        }

        if (entity.getHabitat() != null) {
            dto.habitat(FishDetailsDto.HabitatDto.builder()
                    .habitatGeneral(entity.getHabitat().getHabitatGeneral())
                    .habitatLakes(entity.getHabitat().getHabitatLakes())
                    .habitatRivers(entity.getHabitat().getHabitatRivers())
                    .preferredDepthMin(entity.getHabitat().getPreferredDepthMin())
                    .preferredDepthMax(entity.getHabitat().getPreferredDepthMax())
                    .bottomType(entity.getHabitat().getBottomType())
                    .feedingLayer(entity.getHabitat().getFeedingLayer())
                    .build());
        }

        if (entity.getTackleSetup() != null) {
            dto.tackleSetup(FishDetailsDto.TackleDto.builder()
                    .killerBaits(entity.getTackleSetup().getKillerBaits())
                    .baitSelectionRules(entity.getTackleSetup().getBaitSelectionRules())
                    .setupRod(entity.getTackleSetup().getSetupRod())
                    .setupReel(entity.getTackleSetup().getSetupReel())
                    .setupMainLine(entity.getTackleSetup().getSetupMainLine())
                    .setupLeader(entity.getTackleSetup().getSetupLeader())
                    .setupHook(entity.getTackleSetup().getSetupHook())
                    .setupTerminalTackle(entity.getTackleSetup().getSetupTerminalTackle())
                    .build());
        }

        if (entity.getPolishRecord() != null) {
            dto.polishRecord(FishDetailsDto.RecordDto.builder()
                    .recordWeight(entity.getPolishRecord().getRecordWeight())
                    .recordLength(entity.getPolishRecord().getRecordLength())
                    .recordYear(entity.getPolishRecord().getRecordYear())
                    .build());
        }

        if (entity.getActivityCalendar() != null) {
            dto.activityCalendar(FishDetailsDto.CalendarDto.builder()
                    .janActivity(entity.getActivityCalendar().getJanActivity())
                    .febActivity(entity.getActivityCalendar().getFebActivity())
                    .marActivity(entity.getActivityCalendar().getMarActivity())
                    .aprActivity(entity.getActivityCalendar().getAprActivity())
                    .mayActivity(entity.getActivityCalendar().getMayActivity())
                    .junActivity(entity.getActivityCalendar().getJunActivity())
                    .julActivity(entity.getActivityCalendar().getJulActivity())
                    .augActivity(entity.getActivityCalendar().getAugActivity())
                    .sepActivity(entity.getActivityCalendar().getSepActivity())
                    .octActivity(entity.getActivityCalendar().getOctActivity())
                    .novActivity(entity.getActivityCalendar().getNovActivity())
                    .decActivity(entity.getActivityCalendar().getDecActivity())
                    .build());
        }

        if (entity.getAlgorithmParams() != null) {
            dto.algorithmParams(FishDetailsDto.AlgorithmDto.builder()
                    .tempMinActive(entity.getAlgorithmParams().getTempMinActive())
                    .tempMaxActive(entity.getAlgorithmParams().getTempMaxActive())
                    .tempOptimalMin(entity.getAlgorithmParams().getTempOptimalMin())
                    .tempOptimalMax(entity.getAlgorithmParams().getTempOptimalMax())
                    .preferredPressureTrend(entity.getAlgorithmParams().getPreferredPressureTrend())
                    .preferredMoonPhase(entity.getAlgorithmParams().getPreferredMoonPhase())
                    .preferredTimeOfDay(entity.getAlgorithmParams().getPreferredTimeOfDay())
                    .windGustMax(entity.getAlgorithmParams().getWindGustMax())
                    .weightWaterTemp(entity.getAlgorithmParams().getWeightWaterTemp())
                    .weightPressure(entity.getAlgorithmParams().getWeightPressure())
                    .weightWind(entity.getAlgorithmParams().getWeightWind())
                    .build());
        }

        return dto.build();
    }
}