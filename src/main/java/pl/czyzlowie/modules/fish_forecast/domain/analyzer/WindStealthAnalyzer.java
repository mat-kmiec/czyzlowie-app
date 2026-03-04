package pl.czyzlowie.modules.fish_forecast.domain.analyzer;

import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.fish_forecast.domain.model.FishProfile;
import pl.czyzlowie.modules.fish_forecast.domain.model.SynopSnapshot;
import pl.czyzlowie.modules.fish_forecast.domain.model.WeatherContext;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The WindStealthAnalyzer is responsible for analyzing*/
@Component
public class WindStealthAnalyzer implements WeatherAnalyzer {

    /**
     * Analyzes weather conditions and fish behavior based on the given weather context, fish profile,
     * and target timestamp. The analysis considers factors such as wind speed, wind direction, cloud cover,
     * precipitation, and other parameters derived from the provided fish profile and weather data.
     *
     * The method evaluates the suitability of fishing conditions, calculates a score representing the
     * overall favorability, and provides recommendations or warnings if specific thresholds are exceeded.
     *
     * @param context The {@code WeatherContext} object that contains weather data, including a timeline
     *                of {@code SynopSnapshot} objects for analysis.
     * @param profile The {@code FishProfile} object that contains parameters for evaluating fishing conditions,
     *                such as acceptable wind speeds, rainfall tolerance, and other fish behavior attributes.
     * @param targetTime The {@code LocalDateTime} object that specifies the target timestamp for which
     *                   the analysis should be conducted.
     * @return An {@code AnalyzerResult} object that encapsulates the analysis results, including a favorability score,
     *         weight, dominant analysis factors, and optional advice or recommendations for fishing strategies.
     */
    @Override
    public AnalyzerResult analyze(WeatherContext context, FishProfile profile, LocalDateTime targetTime) {
        int weight = profile.params() != null && profile.params().getWeightWind() != null
                ? profile.params().getWeightWind() : 30;

        List<SynopSnapshot> timeline = context.synopTimeline();
        if (timeline == null || timeline.isEmpty()) {
            return buildEmptyResult(weight);
        }

        SynopSnapshot currentSnap = findClosestSnapshot(timeline, targetTime);
        if (currentSnap == null) {
            return buildEmptyResult(weight);
        }

        if (profile.params() != null) {
            BigDecimal gustMax = profile.params().getWindGustMax();
            BigDecimal rainMax = profile.params().getRainMax();

            if (gustMax != null && currentSnap.windGusts() != null && currentSnap.windGusts().compareTo(gustMax) > 0) {
                return buildVetoResult("Niebezpieczne porywy wiatru (" + currentSnap.windGusts() + " km/h). Zagrożenie na wodzie.", weight);
            }
            if (rainMax != null && currentSnap.precipitation() != null && currentSnap.precipitation().compareTo(rainMax) > 0) {
                return buildVetoResult("Ulewny deszcz (" + currentSnap.precipitation() + " mm). Woda będzie mętna, ryby nie widzą przynęt.", weight);
            }
        }

        double score = 50.0;
        List<String> tips = new ArrayList<>();
        List<String> factors = new ArrayList<>();

        double windSpeed = currentSnap.windSpeed() != null ? currentSnap.windSpeed().doubleValue() : 0.0;
        int windDir = currentSnap.windDirection() != null ? currentSnap.windDirection() : -1;
        int clouds = currentSnap.cloudCover() != null ? currentSnap.cloudCover() : 50;
        double rain = currentSnap.precipitation() != null ? currentSnap.precipitation().doubleValue() : 0.0;

        if (profile.isGeneralBiomass()) {
            if (windSpeed > 5.0 && windSpeed < 20.0) score += 20.0;
            if (windSpeed == 0.0) score -= 10.0;
            if (windSpeed > 30.0) score -= 20.0;
        } else if (profile.params() != null) {
            Double minWind = profile.params().getWindSpeedMin() != null ? profile.params().getWindSpeedMin().doubleValue() : null;
            Double maxWind = profile.params().getWindSpeedMax() != null ? profile.params().getWindSpeedMax().doubleValue() : null;

            if (minWind != null && windSpeed < minWind) score -= 15.0;
            if (maxWind != null && windSpeed > maxWind) score -= 15.0;
            if (minWind != null && maxWind != null && windSpeed >= minWind && windSpeed <= maxWind) score += 25.0;
        }

        if (windDir >= 0) {
            String dirName = getWindDirectionName(windDir);
            factors.add("Wiatr " + dirName);
            if (windDir >= 45 && windDir <= 135) {
                score -= 10.0;
                tips.add("WIATR: Wschodni wiatr zazwyczaj przynosi wysokie ciśnienie i bezrybie. Szukaj ryb głębiej.");
            } else if (windDir >= 225 && windDir <= 315) {
                score += 15.0;
                tips.add("WIATR: Idealny, zachodni lub południowo-zachodni wiatr niosący wilgoć. Ryby żerują chętniej.");
            }
        }

        factors.add("Zachmurzenie: " + clouds + "%");
        if (profile.isGeneralBiomass()) {
            if (clouds > 60) score += 10.0;
            if (clouds < 20 && windSpeed < 5.0) {
                score -= 20.0;
                tips.add("WIDOCZNOŚĆ: Brak chmur i wiatru. Woda jak soczewka. Drapieżniki o wrażliwych oczach (np. sandacz) uciekną głęboko. Używaj małych, naturalnych przynęt.");
            }
        } else if (profile.params() != null) {
            Integer minClouds = profile.params().getCloudCoverMin();
            Integer maxClouds = profile.params().getCloudCoverMax();

            if (minClouds != null && clouds < minClouds) score -= 15.0;
            if (maxClouds != null && clouds > maxClouds) score -= 10.0;
            if (minClouds != null && maxClouds != null && clouds >= minClouds && clouds <= maxClouds) {
                score += 20.0;
                if (clouds > 70) tips.add("KAMUFLAŻ: Duże zachmurzenie. Światło jest rozproszone, ryby czują się bezpiecznie i wypływają na płycizny.");
            }
        }

        if (rain > 0.0 && rain < 5.0) {
            Boolean toleratesRain = profile.params() != null ? profile.params().getToleratesRain() : true;
            if (Boolean.TRUE.equals(toleratesRain)) {
                score += 10.0;
                tips.add("OPAD: Lekki deszcz dotlenia wodę i rozbija taflę. To świetny moment na żerowanie tuż pod powierzchnią.");
            } else {
                score -= 10.0;
            }
        }

        score = Math.min(100.0, Math.max(0.0, score));
        String dominant = String.join(" | ", factors);

        return AnalyzerResult.builder()
                .analyzerName("Wind&Stealth")
                .score(score)
                .weight(weight)
                .dominantFactor(dominant)
                .tackleTips(tips)
                .build();
    }

    /**
     * Determines the wind direction name based on the given angle in degrees.
     * The method evaluates the angle and maps it to a corresponding directional name.
     *
     * @param degrees The wind direction in degrees, ranging from 0 to 360.
     * @return A string representing the wind direction name in Polish, or "Zmienny" if the direction is variable.
     */
    private String getWindDirectionName(int degrees) {
        if (degrees >= 337 || degrees < 22) return "Północny (N)";
        if (degrees >= 22 && degrees < 67) return "Północno-Wschodni (NE)";
        if (degrees >= 67 && degrees < 112) return "Wschodni (E)";
        if (degrees >= 112 && degrees < 157) return "Południowo-Wschodni (SE)";
        if (degrees >= 157 && degrees < 202) return "Południowy (S)";
        if (degrees >= 202 && degrees < 247) return "Południowo-Zachodni (SW)";
        if (degrees >= 247 && degrees < 292) return "Zachodni (W)";
        if (degrees >= 292 && degrees < 337) return "Północno-Zachodni (NW)";
        return "Zmienny";
    }

    /**
     * Finds the snapshot in the timeline that is closest in time to the specified target timestamp.
     *
     * @param timeline A list of {@code SynopSnapshot} objects representing the timeline of weather data.
     *                 Each snapshot contains weather data and a timestamp.
     * @param target   The target {@code LocalDateTime} for which the closest snapshot is to be found.
     * @return         The {@code SynopSnapshot} that is temporally closest to the target timestamp,
     *                 or {@code null} if the timeline is empty.
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
     * Constructs an {@code AnalyzerResult} with predefined properties representing an empty or default analysis result.
     * This result is used when there is insufficient data regarding factors such as wind and cloud cover.
     *
     * @param weight The weight or influence factor to assign to the constructed result.
     * @return An {@code AnalyzerResult} object containing default values for the analysis output.
     */
    private AnalyzerResult buildEmptyResult(int weight) {
        return AnalyzerResult.builder()
                .analyzerName("Wind&Stealth")
                .score(50.0)
                .weight(weight)
                .dominantFactor("Brak danych o wietrze i zachmurzeniu")
                .tackleTips(List.of())
                .build();
    }

    /**
     * Builds an instance of {@code AnalyzerResult} with predefined properties for scenarios
     * where the analysis outcome indicates a veto or threat to proceeding.
     * The method initializes the result with a fixed analyzer name ("Wind&Stealth"),
     * a zero score (0.0), and a dominant factor described by the provided reason.
     * Additionally, it includes a specific recommendation in the tips to indicate
     * that staying indoors is advised.
     *
     * @param reason A string describing the dominant factor or reason for the veto decision.
     * @param weight An integer representing the influence or weight of this veto in the analysis.
     * @return An {@code AnalyzerResult} encapsulating the veto result with predefined and provided details.
     */
    private AnalyzerResult buildVetoResult(String reason, int weight) {
        return AnalyzerResult.builder()
                .analyzerName("Wind&Stealth")
                .score(0.0)
                .weight(weight)
                .dominantFactor(reason)
                .tackleTips(List.of("ZAGROŻENIE/VETO: Zostań w domu!"))
                .build();
    }
}
