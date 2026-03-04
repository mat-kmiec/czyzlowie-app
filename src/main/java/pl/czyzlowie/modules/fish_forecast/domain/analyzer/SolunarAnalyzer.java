package pl.czyzlowie.modules.fish_forecast.domain.analyzer;

import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.fish.entity.enums.TimeOfDay;
import pl.czyzlowie.modules.fish_forecast.domain.model.FishProfile;
import pl.czyzlowie.modules.fish_forecast.domain.model.MoonSnapshot;
import pl.czyzlowie.modules.fish_forecast.domain.model.WeatherContext;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Analyzes solunar and timing conditions to evaluate fishing activity effectiveness
 * based on the provided weather context, fish profile, and target time.
 * This class implements methods for determining active solunar windows,
 * evaluating moon phases, and calculating a score that reflects the probability
 * of successful fishing based on these factors.
 */
@Component
public class SolunarAnalyzer implements WeatherAnalyzer {

    private static final int SOLUNAR_WEIGHT = 40;

    /**
     * Analyzes the provided weather context, fish profile, and target time to determine
     * the solunar and timing conditions that affect fishing activity. It calculates a
     * score based on solunar events, golden hours, and other influencing factors, and
     * returns an `AnalyzerResult` containing this analysis.
     *
     * @param context the weather context containing information such as the moon timeline
     * @param profile the fish profile including species-specific data and preferences
     * @param targetTime the target date and time for the analysis
     * @return an `AnalyzerResult` containing the analysis score, dominant factor, and any relevant tips
     */
    @Override
    public AnalyzerResult analyze(WeatherContext context, FishProfile profile, LocalDateTime targetTime) {
        List<MoonSnapshot> timeline = context.moonTimeline();
        if (timeline == null || timeline.isEmpty()) {
            return buildEmptyResult();
        }

        LocalDate targetDate = targetTime.toLocalDate();
        MoonSnapshot moonData = timeline.stream()
                .filter(m -> m.date().equals(targetDate))
                .findFirst()
                .orElse(null);

        if (moonData == null) {
            return buildEmptyResult();
        }

        double score = 50.0;
        List<String> tips = new ArrayList<>();
        List<String> activeWindows = new ArrayList<>();

        boolean isGoldenHour = false;
        if (moonData.sunrise() != null && isWithinWindow(targetTime, moonData.sunrise(), 60)) {
            score += 25.0;
            isGoldenHour = true;
            activeWindows.add("Poranna Złota Godzina");
        } else if (moonData.sunset() != null && isWithinWindow(targetTime, moonData.sunset(), 60)) {
            score += 25.0;
            isGoldenHour = true;
            activeWindows.add("Wieczorna Złota Godzina");
        }

        boolean isMajorWindow = false;
        boolean isMinorWindow = false;

        if (moonData.transit() != null && isWithinWindow(targetTime, moonData.transit(), 90)) {
            score += 30.0;
            isMajorWindow = true;
            activeWindows.add("Główne Okno Solunarne (Tranzyt)");
        }
        if (moonData.moonrise() != null && isWithinWindow(targetTime, moonData.moonrise(), 45)) {
            score += 15.0;
            isMinorWindow = true;
            activeWindows.add("Pomniejsze Okno (Wschód Księżyca)");
        }
        if (moonData.moonset() != null && isWithinWindow(targetTime, moonData.moonset(), 45)) {
            score += 15.0;
            isMinorWindow = true;
            activeWindows.add("Pomniejsze Okno (Zachód Księżyca)");
        }

        if (isGoldenHour && isMajorWindow) {
            score += 20.0;
            tips.add("JACKPOT: Główne okno żerowania nakłada się na świt/zmierzch! To najlepszy możliwy moment na wodzie. Ryby tracą ostrożność.");
        }

        if (moonData.isSuperMoon() != null && moonData.isSuperMoon()) {
            score += 10.0;
            tips.add("SUPERKSIĘŻYC: Grawitacja działa ze zdwojoną siłą, stymulując ruchy w wodzie.");
        }

        if (!profile.isGeneralBiomass() && profile.params() != null) {
            TimeOfDay preferredTime = profile.params().getPreferredTimeOfDay();
            if (preferredTime != null && preferredTime != TimeOfDay.ANY) {
                if ((preferredTime == TimeOfDay.DAWN || preferredTime == TimeOfDay.MORNING)
                        && activeWindows.contains("Poranna Złota Godzina")) {
                    score += 15.0;
                }
                if (preferredTime == TimeOfDay.DUSK
                        && activeWindows.contains("Wieczorna Złota Godzina")) {
                    score += 15.0;
                }
                if (preferredTime == TimeOfDay.NIGHT
                        && isNightTime(targetTime, moonData.sunrise(), moonData.sunset())) {
                    score += 15.0;
                }
            }

            if (isNightTime(targetTime, moonData.sunrise(), moonData.sunset())) {
                double illum = moonData.illuminationPct() != null ? moonData.illuminationPct().doubleValue() : 50.0;
                Double minIllum = profile.params().getIlluminationMinPct() != null ? profile.params().getIlluminationMinPct().doubleValue() : null;
                Double maxIllum = profile.params().getIlluminationMaxPct() != null ? profile.params().getIlluminationMaxPct().doubleValue() : null;

                if (minIllum != null && illum < minIllum) score -= 20.0;
                if (maxIllum != null && illum > maxIllum) score -= 20.0;

                tips.add(String.format("NOC: Oświetlenie tarczy księżyca wynosi %.0f%%.", illum));
            }
        }

        score = Math.min(100.0, Math.max(0.0, score));

        if (isGoldenHour) {
            tips.add("TAKTYKA: W czasie złotej godziny używaj przynęt tuż pod powierzchnią (topwater, poppery, smużaki) - ryby polują wzrokowo na tle jasnego nieba.");
        }
        if (score < 40.0) {
            tips.add("FAZA PASYWNA: Jesteśmy poza głównymi oknami żerowania. Szukaj ryb głębiej, w ich naturalnych kryjówkach, i podawaj przynętę wolniej.");
        }

        String dominant = activeWindows.isEmpty() ? "Brak aktywnych okien solunarnych" : "Aktywne: " + String.join(", ", activeWindows);

        return AnalyzerResult.builder()
                .analyzerName("Solunar&Timing")
                .score(score)
                .weight(SOLUNAR_WEIGHT)
                .dominantFactor(dominant)
                .tackleTips(tips)
                .build();
    }

    /**
     * Determines if a given target time is within a specified time window, in minutes, of an event time.
     *
     * @param targetTime the reference time to compare against
     * @param eventTime the time of the event to compare to the target time; must not be null
     * @param windowMinutes the allowable window, in minutes, around the target time
     * @return true if the event time is within the specified window in relation to*/
    private boolean isWithinWindow(LocalDateTime targetTime, LocalDateTime eventTime, int windowMinutes) {
        if (eventTime == null) return false;
        long minutesDiff = Math.abs(Duration.between(targetTime, eventTime).toMinutes());
        return minutesDiff <= windowMinutes;
    }

    /**
     * Determines whether the given target time occurs during night time.
     * It compares the target time with the provided sunrise and sunset times.
     *
     * @param target the time to check
     * @param sunrise the time of sunrise, used as the start of day
     * @param sunset the time of sunset, used as the end of day
     * @return true if the target time is either before sunrise or after sunset, false otherwise
     */
    private boolean isNightTime(LocalDateTime target, LocalDateTime sunrise, LocalDateTime sunset) {
        if (sunrise == null || sunset == null) return false;
        return target.isBefore(sunrise) || target.isAfter(sunset);
    }

    /**
     * Constructs and returns an empty AnalyzerResult instance with predefined default values.
     * This method is used when there is insufficient astronomical data to perform an analysis.
     *
     * @return a new instance of AnalyzerResult populated with:
     *         - analyzerName: "Solunar&Timing"
     *         - score: 50.0
     *         - weight: 10
     *         - dominantFactor: "Brak danych astronomicznych"
     *         - tackleTips: an empty list
     */
    private AnalyzerResult buildEmptyResult() {
        return AnalyzerResult.builder()
                .analyzerName("Solunar&Timing")
                .score(50.0)
                .weight(10)
                .dominantFactor("Brak danych astronomicznych")
                .tackleTips(List.of())
                .build();
    }
}