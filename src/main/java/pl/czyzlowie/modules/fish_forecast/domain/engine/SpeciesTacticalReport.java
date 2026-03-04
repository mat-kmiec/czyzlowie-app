package pl.czyzlowie.modules.fish_forecast.domain.engine;

import lombok.Builder;
import java.util.List;

/**
 * Represents a comprehensive tactical report related to a specific species.
 *
 * The class is designed to encapsulate various properties and scores derived from
 * tactical and environmental analyses. It provides detailed information regarding
 * the species, their attributes, corresponding scores, and strategic tips for
 * specific conditions. This information may be used for analysis, prediction,
 * or other decision-making processes.
 *
 * Components:
 * - speciesId: The unique identifier for the species.
 * - speciesName: The name of the species.
 * - totalScore: A cumulative score representing the overall success factor for the species in the given environment.
 * - zAxisLocation: The relative z-axis position of the species in its environment.
 * - migrationVector: The directional movement pattern of the species.
 * - preySize: The typical size of the prey associated with the species.
 * - metabolismState: The state of the species' metabolism, indicating activity levels.
 * - waterPenetration: The ability of the species to move or navigate in water environments.
 * - suggestedColors: A list of suggested colors for strategic or tactical relevance.
 * - acoustics: Describes sound-related information relevant to the species.
 * - tackleBallistics: Refers to ballistics data relevant to tackling or capturing the species.
 * - pressureScore: A score representing the pressure-related environmental impact on the species.
 * - thermalHydroScore: A score representing thermal and hydro conditions affecting the species.
 * - solunarScore: A score derived from solunar conditions and their influence on the species.
 * - windStealthScore: A score reflecting stealth and visibility associated with wind conditions.
 * - combinedTips: A collection of general tactical tips generated from combined analyses.
 * - solunarTips: A set of tips based specifically on solunar data.
 * - windTips: A list of tips derived from wind condition analyses.
 */
@Builder
public record SpeciesTacticalReport(
        Long speciesId,
        String speciesName,
        double totalScore,
        String zAxisLocation,
        String migrationVector,
        String preySize,
        String metabolismState,
        String waterPenetration,
        List<String> suggestedColors,
        String acoustics,
        String tackleBallistics,
        double pressureScore,
        double thermalHydroScore,
        double solunarScore,
        double windStealthScore,
        List<String> combinedTips,
        List<String> solunarTips,
        List<String> windTips
) {}