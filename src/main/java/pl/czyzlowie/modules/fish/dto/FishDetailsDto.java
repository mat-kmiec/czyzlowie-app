package pl.czyzlowie.modules.fish.dto;

import lombok.Builder;
import lombok.Data;
import pl.czyzlowie.modules.fish.entity.enums.*;
import pl.czyzlowie.modules.moon.entity.Enums.MoonPhaseType;

import java.math.BigDecimal;

/**
 * A comprehensive Data Transfer Object (DTO) that provides the full set of information
 * for a specific fish species.
 *
 * This DTO is the primary data source for the detailed view in the fish atlas. It
 * aggregates basic identification data, biological descriptions, angling tips,
 * official regulations, and technical parameters for the activity algorithm.
 *
 * The class contains several static nested DTOs to maintain a clean, structured
 * hierarchy corresponding to the different sections of the user interface:
 *
 * - HabitatDto: Details about water preferences, depth, and substrate.
 * - TackleDto: Recommendations for equipment and bait selection.
 * - RecordDto: Historical maximums for the species in Poland.
 * - PzwDto: Legal fishing rules and protection periods.
 * - CalendarDto: Monthly biological activity baseline.
 * - AlgorithmDto: Specific environmental thresholds used for scoring predictions.
 *
 * This object is typically mapped from a fully initialized FishSpecies entity
 * (usually fetched with an EntityGraph to avoid N+1 queries).
 */
@Data
@Builder
public class FishDetailsDto {
    private String name;
    private String latinName;
    private String englishName;
    private String slug;
    private String imgUrl;
    private FishCategory category;
    private String description;
    private HabitatDto habitat;
    private TackleDto tackleSetup;
    private RecordDto polishRecord;
    private PzwDto pzwRegulations;
    private CalendarDto activityCalendar;
    private AlgorithmDto algorithmParams;

    @Data @Builder public static class HabitatDto {
        private String habitatGeneral;
        private String habitatLakes;
        private String habitatRivers;
        private BigDecimal preferredDepthMin;
        private BigDecimal preferredDepthMax;
        private String bottomType;
        private String feedingLayer;
    }

    @Data @Builder public static class TackleDto {
        private String killerBaits;
        private String baitSelectionRules;
        private String setupRod;
        private String setupReel;
        private String setupMainLine;
        private String setupLeader;
        private String setupHook;
        private String setupTerminalTackle;
    }

    @Data @Builder public static class RecordDto {
        private Double recordWeight;
        private Double recordLength;
        private Integer recordYear;
    }

    @Data @Builder public static class PzwDto {
        private Integer minDimension;
        private Integer maxDimension;
        private String dimensionExceptions;
        private String protectionPeriod;
        private String spawningSeason;
        private Integer dailyLimitPieces;
        private Double dailyLimitWeight;
        private String sharedLimitGroup;
        private String additionalRules;
    }

    @Data @Builder public static class CalendarDto {
        private ActivityLevel janActivity;
        private ActivityLevel febActivity;
        private ActivityLevel marActivity;
        private ActivityLevel aprActivity;
        private ActivityLevel mayActivity;
        private ActivityLevel junActivity;
        private ActivityLevel julActivity;
        private ActivityLevel augActivity;
        private ActivityLevel sepActivity;
        private ActivityLevel octActivity;
        private ActivityLevel novActivity;
        private ActivityLevel decActivity;
    }

    @Data @Builder public static class AlgorithmDto {
        private BigDecimal tempMinActive;
        private BigDecimal tempMaxActive;
        private BigDecimal tempOptimalMin;
        private BigDecimal tempOptimalMax;
        private PressureTrend preferredPressureTrend;
        private MoonPhaseType preferredMoonPhase;
        private TimeOfDay preferredTimeOfDay;
        private BigDecimal windGustMax;
        private Integer weightWaterTemp;
        private Integer weightPressure;
        private Integer weightWind;
    }
}
