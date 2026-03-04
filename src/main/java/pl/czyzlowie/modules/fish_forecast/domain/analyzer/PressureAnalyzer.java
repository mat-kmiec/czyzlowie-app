package pl.czyzlowie.modules.fish_forecast.domain.analyzer;

import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.fish.entity.enums.FishCategory;
import pl.czyzlowie.modules.fish.entity.enums.PressureTrend;
import pl.czyzlowie.modules.fish_forecast.domain.model.FishProfile;
import pl.czyzlowie.modules.fish_forecast.domain.model.SynopSnapshot;
import pl.czyzlowie.modules.fish_forecast.domain.model.WeatherContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The PressureAnalyzer class is responsible for analyzing weather pressure trends and their impact
 * on fishing*/
@Component
public class PressureAnalyzer implements WeatherAnalyzer {

    private static final int TREND_WINDOW_HOURS = 12;
    private static final int MAX_ALLOWED_GAP_MINUTES = 120;

    /**
     * Analyzes the given weather context and fish profile to determine the barometric pressure trend,
     * calculate a score, and provide related fishing advice.
     *
     * @param context the weather context containing synoptic data for analysis
     * @param profile the fish profile containing parameters relevant for analysis
     * @param targetTime the time for which the analysis should be performed
     * @return an {@code AnalyzerResult} object containing the analysis results, including score, weight,
     *         dominant factor, and tackle tips
     */
    @Override
    public AnalyzerResult analyze(WeatherContext context, FishProfile profile, LocalDateTime targetTime) {
        int weight = profile.params() != null && profile.params().getWeightPressure() != null
                ? profile.params().getWeightPressure() : 50;

        List<SynopSnapshot> timeline = context.synopTimeline();
        if (timeline == null || timeline.isEmpty()) {
            return buildEmptyResult(weight, "Brak bazy danych synoptycznych dla wybranego okresu.");
        }

        SynopSnapshot currentSnap = findClosestSnapshot(timeline, targetTime);
        LocalDateTime pastTarget = targetTime.minusHours(TREND_WINDOW_HOURS);
        SynopSnapshot pastSnap = findClosestSnapshot(timeline, pastTarget);

        if (!isValidSnapshot(currentSnap, targetTime) || !isValidSnapshot(pastSnap, pastTarget)) {
            return buildEmptyResult(weight, "Zbyt duża luka w danych telemetrycznych, by wyliczyć trend.");
        }

        double currentPressure = currentSnap.pressure().doubleValue();
        double pastPressure = pastSnap.pressure().doubleValue();
        double deltaP = currentPressure - pastPressure;

        pl.czyzlowie.modules.barometer.entity.PressureTrend physicalTrend = calculatePhysicalTrend(deltaP);
        double score = calculateScore(physicalTrend, currentPressure, profile);

        String dominantFactor = generateDominantFactor(physicalTrend, deltaP, currentPressure);
        List<String> tackleTips = generateTackleTips(physicalTrend, profile);

        return AnalyzerResult.builder()
                .analyzerName("BarometricPressure")
                .score(score)
                .weight(weight)
                .dominantFactor(dominantFactor)
                .tackleTips(tackleTips)
                .build();
    }

    /**
     * Validates whether a given synoptic weather data snapshot is acceptable for analysis
     * based on its pressure value and the time difference relative to a target timestamp.
     *
     * @param snap   The synoptic weather data snapshot to validate. Must not be null, and
     *               its pressure property must not be null.
     * @param target The target timestamp to compare with the snapshot's timestamp.
     * @return true if the snapshot is valid*/
    private boolean isValidSnapshot(SynopSnapshot snap, LocalDateTime target) {
        if (snap == null || snap.pressure() == null) return false;
        long diff = Math.abs(java.time.Duration.between(snap.timestamp(), target).toMinutes());
        return diff <= MAX_ALLOWED_GAP_MINUTES;
    }

    /**
     * Determines the physical trend of barometric pressure changes based on the given*/
    private pl.czyzlowie.modules.barometer.entity.PressureTrend calculatePhysicalTrend(double deltaP) {
        if (deltaP >= 4.0) return pl.czyzlowie.modules.barometer.entity.PressureTrend.RISING_FAST;
        if (deltaP > 1.0) return pl.czyzlowie.modules.barometer.entity.PressureTrend.RISING;
        if (deltaP <= -4.0) return pl.czyzlowie.modules.barometer.entity.PressureTrend.FALLING_FAST;
        if (deltaP < -1.0) return pl.czyzlowie.modules.barometer.entity.PressureTrend.FALLING;
        return pl.czyzlowie.modules.barometer.entity.PressureTrend.STABLE;
    }

    /**
     * Calculates a score based on the barometric pressure trend, current pressure, and fish profile.
     * The score determines the suitability of the given conditions for the specified fish behavior patterns.
     *
     * @param physicalTrend The observed barometric pressure trend, represented as a {@code PressureTrend} enum.
     *                      Possible values include STABLE, FALLING, RISING, FALLING_FAST, and RISING_FAST.
     * @param currentPressure The current atmospheric pressure in hectopascals (hPa). Must be a valid double value.
     * @param profile The {@code FishProfile} representing the fish's behavioral preferences, including pressure
     *                tolerance and preferred pressure trends. Must not be null.
     * @return A double value representing the calculated score. Higher scores indicate more favorable conditions.
     */
    private double calculateScore(pl.czyzlowie.modules.barometer.entity.PressureTrend physicalTrend, double currentPressure, FishProfile profile) {
        double score = 50.0;

        if (profile.isGeneralBiomass()) {
            return switch (physicalTrend) {
                case STABLE -> 90.0;
                case FALLING -> 80.0;
                case RISING -> 40.0;
                case FALLING_FAST, RISING_FAST -> 10.0;
            };
        }

        PressureTrend preferredTrend = profile.params().getPreferredPressureTrend();

        if (profile.params().getPressureMin() != null && currentPressure < profile.params().getPressureMin().doubleValue()) {
            return 10.0;
        }
        if (profile.params().getPressureMax() != null && currentPressure > profile.params().getPressureMax().doubleValue()) {
            return 10.0;
        }

        if (preferredTrend == PressureTrend.ANY) return 80.0;

        switch (physicalTrend) {
            case STABLE:
                if (currentPressure >= 1015) {
                    score = (preferredTrend == PressureTrend.STABLE_HIGH) ? 100.0 : 60.0;
                } else {
                    score = (preferredTrend == PressureTrend.STABLE_LOW) ? 100.0 : 40.0;
                }
                break;
            case FALLING:
                score = (preferredTrend == PressureTrend.FALLING) ? 100.0 : 40.0;
                break;
            case RISING:
                score = (preferredTrend == PressureTrend.RISING) ? 100.0 : 30.0;
                break;
            case FALLING_FAST:
            case RISING_FAST:
                score = (preferredTrend == PressureTrend.FLUCTUATING) ? 90.0 : 5.0;
                break;
        }

        return score;
    }

    /**
     * Generates a descriptive message representing the dominant factor based on the barometric pressure trend,
     * the pressure change rate (delta), and the current pressure.
     *
     * The message includes the translated description of the pressure trend, the magnitude of the pressure
     * change over a specified time period, and the current pressure value.
     *
     * @param trend the barometric pressure trend to describe. Must not be null. Supported values are:
     *              RISING_FAST, RISING, STABLE, FALLING, FALLING_FAST.
     * @param delta the rate of pressure change over a 12-hour period, measured in hPa.
     *              Represents the magnitude of the change.
     * @param current the current barometric pressure value, measured in hPa.
     * @return a string describing the dominant factor, including the trend, pressure change rate,
     *         and current pressure.
     */
    private String generateDominantFactor(pl.czyzlowie.modules.barometer.entity.PressureTrend trend, double delta, double current) {
        return String.format("%s ciśnienia (%.1f hPa/12h). Obecnie: %.0f hPa",
                translateTrend(trend), delta, current);
    }

    /**
     * Generates a list of tackle tips based on the barometric pressure trend and the fish profile.
     *
     * The method evaluates the pressure trend and fish profile attributes such as the category
     * and general biomass flag to create relevant fishing tackle recommendations.
     *
     * @param trend   the current barometric pressure trend, provided as a {@code PressureTrend} enum.
     *                Possible values include RISING_FAST, RISING, STABLE, FALLING, and FALLING_FAST.
     * @param profile the fish profile containing details about the species, including its general biomass
     *                status and fish category. Must not be null.
     * @return a list of strings, each containing specific tackle tips that correspond to the provided
     *         pressure trend and fish profile. Returns an empty list if no recommendations apply.
     */
    private List<String> generateTackleTips(pl.czyzlowie.modules.barometer.entity.PressureTrend trend, FishProfile profile) {
        List<String> tips = new ArrayList<>();

        if (profile.isGeneralBiomass()) {
            if (trend == pl.czyzlowie.modules.barometer.entity.PressureTrend.FALLING) {
                tips.add("Nadchodzi front. Ryby mogą intensywnie żerować tuż przed burzą. Zastosuj aktywniejsze metody (spinning, feeder z częstym rzucaniem).");
            } else if (trend == pl.czyzlowie.modules.barometer.entity.PressureTrend.STABLE) {
                tips.add("Ciśnienie ustabilizowane. Idealne warunki na długie zasiadki karpiowe, gruntowe i spokojny spławik.");
            }
            return tips;
        }

        FishCategory category = profile.category();

        if (category == FishCategory.PREDATOR) {
            if (trend == pl.czyzlowie.modules.barometer.entity.PressureTrend.FALLING) {
                tips.add("SPINNING: Ciśnienie spada, drapieżniki ruszają na łowy! Użyj głośnych, agresywnych przynęt (woblery, błystki) w płytkich strefach.");
                tips.add("ŻYWIEC: Ustaw grunt płycej, ryby drapieżne podnoszą się do góry toni wodnej.");
            } else if (trend == pl.czyzlowie.modules.barometer.entity.PressureTrend.RISING || trend == pl.czyzlowie.modules.barometer.entity.PressureTrend.STABLE) {
                tips.add("SPINNING: Ryba wklejona w dno. Prowadź przynęty gumowe (jaskółki, kopyta) w opadzie, bardzo blisko dna.");
            }
        } else {
            if (trend == pl.czyzlowie.modules.barometer.entity.PressureTrend.STABLE) {
                tips.add("GRUNT/KARPIÓWKA: Świetny czas na grube nęcenie. Ryby żerują miarowo. Zbuduj dywan zanętowy.");
                tips.add("SPŁAWIK: Użyj lżejszych zestawów, brania mogą być pewne i regularne.");
            } else if (trend == pl.czyzlowie.modules.barometer.entity.PressureTrend.FALLING_FAST || trend == pl.czyzlowie.modules.barometer.entity.PressureTrend.RISING_FAST) {
                tips.add("METHOD FEEDER: Użyj małych, fluoroscencyjnych waftersów by sprowokować ospałe ryby. Ryba zamknęła pyski, omijaj grube nęcenie.");
            }
        }

        return tips;
    }

    /**
     * Translates the given barometric pressure trend into a descriptive string.
     *
     * This method maps the provided {@link pl.czyzlowie.modules.barometer.entity.PressureTrend}
     * enum to its corresponding description in Polish. The descriptions provide a textual
     * representation of the barometric pressure changes for better readability.
     *
     * @param trend the barometric pressure trend to translate. Must not be null.
     *              Possible values are RISING_FAST, RISING, STABLE, FALLING, FALLING_FAST.
     * @return a string describing the pressure trend in Polish based on the provided enum value.
     */
    private String translateTrend(pl.czyzlowie.modules.barometer.entity.PressureTrend trend) {
        return switch (trend) {
            case RISING_FAST -> "Gwałtowny wzrost";
            case RISING -> "Wzrost";
            case STABLE -> "Stabilne";
            case FALLING -> "Spadek";
            case FALLING_FAST -> "Gwałtowny spadek";
        };
    }

    /**
     * Finds the closest synoptic weather data snapshot to the specified target time from a given timeline.
     * The method calculates the time difference between the target timestamp and each snapshot's timestamp,
     * and selects the snapshot with the smallest difference.
     *
     * @param timeline A list of {@code SynopSnapshot} objects representing the timeline of weather data snapshots.
     *                 Must not be null but can be empty.
     * @param target   The target {@code LocalDateTime} for which the closest snapshot should be found.
     *                 Must not be null.
     * @return The {@code SynopSnapshot} closest to the target timestamp, or {@code null} if the timeline is empty.
     */
    private SynopSnapshot findClosestSnapshot(List<SynopSnapshot> timeline, LocalDateTime target) {
        return timeline.stream()
                .min((a, b) -> {
                    long diffA = Math.abs(java.time.Duration.between(a.timestamp(), target).toMinutes());
                    long diffB = Math.abs(java.time.Duration.between(b.timestamp(), target).toMinutes());
                    return Long.compare(diffA, diffB);
                })
                .orElse(null);
    }

    /**
     * Constructs an instance of {@code AnalyzerResult} with default values for an empty analysis result.
     * The result includes a fixed analyzer name, score, the specified weight, and dominant factor message,
     * with an empty list of tackle tips.
     *
     * @param weight  The weight or influence factor for the analysis result.
     * @param message The dominant factor message describing the primary observation or influence in the result.
     * @return An instance of {@code AnalyzerResult} with the specified parameters and fixed default values for other fields.
     */
    private AnalyzerResult buildEmptyResult(int weight, String message) {
        return AnalyzerResult.builder()
                .analyzerName("BarometricPressure")
                .score(50.0)
                .weight(weight)
                .dominantFactor(message)
                .tackleTips(List.of())
                .build();
    }
}
