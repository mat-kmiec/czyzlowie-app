package pl.czyzlowie.modules.fish_forecast.domain.analyzer;

import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.fish.entity.enums.WaterLevelTrend;
import pl.czyzlowie.modules.fish_forecast.domain.model.FishProfile;
import pl.czyzlowie.modules.fish_forecast.domain.model.HydroSnapshot;
import pl.czyzlowie.modules.fish_forecast.domain.model.WeatherContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The WaterThermalAnalyzer class provides an implementation for analyzing fishing conditions
 * based on hydrological and thermal data. This class factors in environmental parameters,
 * fish species behaviors, and trends like water level changes to determine tactical recommendations
 * and suitability scores for fishing scenarios. It also evaluates dominant water conditions
 * influencing fishing potential.
 *
 * Fields:
 * - TREND_WINDOW_HOURS: A constant describing the time window (in hours) used for trend analysis.
 *
 * Inherits:
 * - java.lang.Object
 * - pl.czyzlowie.modules.fish_forecast.domain.analyzer.WeatherAnalyzer
 */
@Component
public class WaterThermalAnalyzer implements WeatherAnalyzer {

    private static final int TREND_WINDOW_HOURS = 24;

    /**
     * Analyzes the fishing conditions based on the given weather context, fish profile,
     * and target time. The analysis incorporates various environmental factors such as
     * water temperature, water level, and their trends over time to provide a score and
     * related fishing recommendations.
     *
     * @param context The weather context containing hydrographic data and related information.
     * @param profile The fish profile containing species-specific parameters and preferences.
     * @param targetTime The time for which the conditions are analyzed.
     * @return An instance of {@code AnalyzerResult} containing the analysis score, weight,
     *         dominant factor, and fishing tips.
     */
    @Override
    public AnalyzerResult analyze(WeatherContext context, FishProfile profile, LocalDateTime targetTime) {
        int weightTemp = profile.params() != null && profile.params().getWeightWaterTemp() != null
                ? profile.params().getWeightWaterTemp() : 50;
        int weightLevel = profile.params() != null && profile.params().getWeightWaterLevel() != null
                ? profile.params().getWeightWaterLevel() : 30;

        int totalWeight = (weightTemp + weightLevel) / 2;

        List<HydroSnapshot> timeline = context.hydroTimeline();
        if (timeline == null || timeline.isEmpty()) {
            return buildEmptyResult(totalWeight);
        }

        HydroSnapshot currentSnap = findClosestSnapshot(timeline, targetTime);
        HydroSnapshot pastSnap = findClosestSnapshot(timeline, targetTime.minusHours(TREND_WINDOW_HOURS));

        if (currentSnap == null) {
            return buildEmptyResult(totalWeight);
        }

        if (profile.params() != null && profile.params().getDischargeMax() != null && currentSnap.discharge() != null) {
            if (currentSnap.discharge().compareTo(profile.params().getDischargeMax()) > 0) {
                return buildVetoResult("Ekstremalnie wysoki przepływ rzeki (przekroczono limit gatunku). Woda niesie muł, ryby kryją się w starorzeczach.", totalWeight);
            }
        }

        double tempScore = calculateThermalScore(currentSnap, profile);
        double levelScore = 50.0;
        WaterLevelTrend physicalTrend = WaterLevelTrend.STABLE;
        double deltaH = 0.0;

        if (pastSnap != null && currentSnap.waterLevel() != null && pastSnap.waterLevel() != null) {
            deltaH = currentSnap.waterLevel() - pastSnap.waterLevel();
            physicalTrend = calculateLevelTrend(deltaH);
            levelScore = calculateLevelScore(physicalTrend, profile);
        }

        double finalScore = ((tempScore * weightTemp) + (levelScore * weightLevel)) / Math.max(1, (weightTemp + weightLevel));

        String dominantFactor = generateDominantFactor(currentSnap.waterTemperature(), physicalTrend, deltaH);
        List<String> tackleTips = generateTacticalTips(currentSnap.waterTemperature(), physicalTrend, profile);

        return AnalyzerResult.builder()
                .analyzerName("WaterAndThermal")
                .score(finalScore)
                .weight(totalWeight)
                .dominantFactor(dominantFactor)
                .tackleTips(tackleTips)
                .build();
    }

    /**
     * Calculates a thermal score based on the current water temperature and the fish profile parameters.
     * The score reflects how suitable the current water temperature is for the target species or general biomass.
     *
     * @param currentSnap the current hydrographic snapshot containing environmental parameters, including water temperature
     * @param profile the fish profile containing ecological or biological parameters for the species being evaluated
     * @return a thermal suitability score as a double, where higher values indicate better suitability of the temperature range
     */
    private double calculateThermalScore(HydroSnapshot currentSnap, FishProfile profile) {
        if (currentSnap.waterTemperature() == null) return 50.0;
        double currentTemp = currentSnap.waterTemperature().doubleValue();

        if (profile.isGeneralBiomass()) {
            if (currentTemp < 4.0) return 10.0;
            if (currentTemp > 25.0) return 20.0;
            if (currentTemp >= 12.0 && currentTemp <= 22.0) return 90.0;
            return 60.0;
        }

        Double minActive = profile.params().getTempMinActive() != null ? profile.params().getTempMinActive().doubleValue() : null;
        Double maxActive = profile.params().getTempMaxActive() != null ? profile.params().getTempMaxActive().doubleValue() : null;
        Double optMin = profile.params().getTempOptimalMin() != null ? profile.params().getTempOptimalMin().doubleValue() : null;
        Double optMax = profile.params().getTempOptimalMax() != null ? profile.params().getTempOptimalMax().doubleValue() : null;

        if (minActive != null && currentTemp < minActive) return 5.0;
        if (maxActive != null && currentTemp > maxActive) return 5.0;

        if (optMin != null && optMax != null) {
            if (currentTemp >= optMin && currentTemp <= optMax) {
                return 100.0;
            }
            double distanceToOpt = Math.min(Math.abs(currentTemp - optMin), Math.abs(currentTemp - optMax));
            return Math.max(30.0, 100.0 - (distanceToOpt * 10.0));
        }

        return 50.0;
    }

    /**
     * Determines the trend of water level based on the provided change in level (deltaH).
     *
     * @param deltaH the change in water level. A positive value indicates an increase in water level,
     *               while a negative value indicates a decrease.
     * @return the water level trend, which can be RISING, FALLING, or STABLE based on the deltaH value.
     */
    private WaterLevelTrend calculateLevelTrend(double deltaH) {
        if (deltaH >= 5.0) return WaterLevelTrend.RISING;
        if (deltaH <= -5.0) return WaterLevelTrend.FALLING;
        return WaterLevelTrend.STABLE;
    }

    /**
     * Calculates the level score based on the water level trend and the fish profile.
     *
     * The method determines the score by taking into account whether the fish profile
     * belongs to the general biomass category, or the preferred water level trend specified
     * in the profile. Different trends such as rising, stable, and falling result in different
     * score values, reflecting the fish's suitability for the given water conditions.
     *
     * @param physicalTrend the current trend of water levels, represented as {@code WaterLevelTrend}
     * @param profile the fish profile containing algorithmic parameters and biomass-related information
     * @return the calculated score as a {@code double}, representing the suitability of the water condition
     */
    private double calculateLevelScore(WaterLevelTrend physicalTrend, FishProfile profile) {
        if (profile.isGeneralBiomass()) {
            return switch (physicalTrend) {
                case RISING -> 80.0;
                case STABLE -> 60.0;
                case FALLING -> 30.0;
                default -> 50.0;
            };
        }

        WaterLevelTrend preferred = profile.params().getPreferredWaterLevelTrend();
        if (preferred == null) return 50.0;

        if (physicalTrend == preferred) return 100.0;
        if (physicalTrend == WaterLevelTrend.FALLING) return 20.0;

        return 50.0;
    }

    /**
     * Generates a list of tactical tips based on water temperature, water level trend, and fish profile.
     *
     * This method provides actionable recommendations for fishing tactics and fish localization.
     * Tips are determined using water temperature thresholds, level trends, and general principles
     * for fish behavior under varying hydrological conditions.
     *
     * @param waterTemp The water temperature, represented as a {@code BigDecimal}. If {@code null},
     *                  generic guidance is included due to a lack of thermal data.
     * @param levelTrend The trend in water levels, represented by the {@code WaterLevelTrend} enum.
     *                   It indicates whether the water level is rising, falling, or stable.
     * @param profile The {@code FishProfile} providing species-specific behavior information.
     *                This parameter allows tailoring tips to the specific fish targeted.
     * @return A {@code List<String>} containing fishing tips, including localization strategies
     *         and tactics, based on the provided data.
     */
    private List<String> generateTacticalTips(BigDecimal waterTemp, WaterLevelTrend levelTrend, FishProfile profile) {
        List<String> tips = new ArrayList<>();

        if (waterTemp != null) {
            double temp = waterTemp.doubleValue();
            if (temp < 8.0) {
                tips.add("LOKALIZACJA: Woda jest zimna. Ryby grupują się w najgłębszych dołkach (zimowiskach). Szukaj ich na przegłębieniach.");
                tips.add("TAKTYKA: Znacznie spowolnij prowadzenie przynęty. Ryby mają spowolniony metabolizm.");
            } else if (temp > 22.0) {
                tips.add("LOKALIZACJA: Woda jest bardzo ciepła. W ciągu dnia ryby mogą uciekać głęboko w poszukiwaniu tlenu i cienia, lub stać w nurcie (natlenienie).");
            } else {
                tips.add("LOKALIZACJA: Temperatura optymalna. Ryby mogą swobodnie penetrować płycizny i strefę przybrzeżną (litoral).");
            }
        } else {
            tips.add("LOKALIZACJA: Brak danych termicznych akwenu. Szukaj drapieżnika zgodnie z ogólnymi zasadami dla obecnej pory roku.");
        }

        if (levelTrend == WaterLevelTrend.RISING) {
            tips.add("HYDRO: Woda przybiera. Szukaj ryb blisko zalanych traw i brzegów – tam wypłukiwany jest naturalny pokarm.");
        } else if (levelTrend == WaterLevelTrend.FALLING) {
            tips.add("HYDRO: Woda opada. Ryby wycofują się z płycizn do głównego nurtu. Skup się na rynnach i krawędziach spadków na głębszą wodę.");
        }

        return tips;
    }

    /**
     * Generates a summary describing the dominant factors influencing water conditions based on
     * temperature, water level trend, and level changes.
     *
     * The method constructs a string representation detailing the current water temperature
     * and the trend of water level changes. When temperature data is unavailable, it reflects
     * this by indicating "Brak danych". Water level trends are formatted with trend type and
     * quantified changes where applicable.
     *
     * @param temp the water temperature in degrees Celsius, or {@code null} if unavailable
     * @param trend the trend of the water level change, represented as {@code WaterLevelTrend}
     * @param deltaH the magnitude of water level change (in centimeters) tied to the trend
     * @return a string summarizing the water temperature and level trend information
     */
    private String generateDominantFactor(BigDecimal temp, WaterLevelTrend trend, double deltaH) {
        String tempStr = temp != null ? temp.toString() + "°C" : "Brak danych";
        String trendStr = switch (trend) {
            case RISING -> "Rosnący (+" + deltaH + " cm)";
            case FALLING -> "Opadający (" + deltaH + " cm)";
            case STABLE -> "Stabilny";
            default -> "Nieokreślony";
        };
        return String.format("Temp. wody: %s | Stan wody: %s", tempStr, trendStr);
    }

    /**
     * Finds the closest hydrological snapshot in the given timeline to the specified target time.
     * The method calculates the absolute time difference between the target and each snapshot,
     * selecting the snapshot with the smallest difference.
     *
     * @param timeline A list of {@code HydroSnapshot} objects representing the timeline of hydrological data.
     * @param target The target {@code LocalDateTime} to which the closest snapshot is determined.
     * @return The {@code HydroSnapshot} closest to the target time, or {@code null} if the timeline is empty.
     */
    private HydroSnapshot findClosestSnapshot(List<HydroSnapshot> timeline, LocalDateTime target) {
        return timeline.stream()
                .min((a, b) -> {
                    long diffA = Math.abs(java.time.Duration.between(a.timestamp(), target).toMinutes());
                    long diffB = Math.abs(java.time.Duration.between(b.timestamp(), target).toMinutes());
                    return Long.compare(diffA, diffB);
                })
                .orElse(null);
    }

    /**
     * Builds an empty analysis result instance for the Water and Thermal analyzer.
     * This result is used in cases where insufficient sensor data is available
     * to perform a meaningful analysis.
     *
     * @param weight the weight of the analyzer's assessment in the overall evaluation
     * @return an instance of {@code AnalyzerResult} with default values, indicating a lack of sufficient data
     */
    private AnalyzerResult buildEmptyResult(int weight) {
        return AnalyzerResult.builder()
                .analyzerName("WaterAndThermal")
                .score(50.0)
                .weight(weight)
                .dominantFactor("Brak wystarczających danych z czujników HYDRO")
                .tackleTips(List.of())
                .build();
    }

    /**
     * Creates an instance of {@code AnalyzerResult} representing a veto decision.
     * This method constructs a result with fixed parameters tailored for situations
     * where conditions are unsuitable for fishing due to hydrological risks.
     *
     * @param reason The reason describing the dominant factor for the veto decision.
     * @param weight The weight or influence factor assigned to this analysis result.
     * @return An {@code AnalyzerResult} object encapsulating the veto decision,
     *         including a fixed analyzer name, score, and predefined tips.
     */
    private AnalyzerResult buildVetoResult(String reason, int weight) {
        return AnalyzerResult.builder()
                .analyzerName("WaterAndThermal")
                .score(0.0)
                .weight(weight)
                .dominantFactor(reason)
                .tackleTips(List.of("ZAGROŻENIE/VETO: Zostań w domu. Warunki hydrologiczne uniemożliwiają skuteczne łowienie."))
                .build();
    }
}