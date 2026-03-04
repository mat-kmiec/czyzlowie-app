package pl.czyzlowie.modules.fish_forecast.domain.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.fish_forecast.domain.analyzer.*;
import pl.czyzlowie.modules.fish_forecast.domain.model.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 * The ForecastEngine class provides functionality to analyze and generate weather forecasts,
 * fishing tactical reports, and other angling-related insights based on environmental data,
 * species profiles, and specific target times. It utilizes various analyzers for pressure,
 * water thermal conditions, solunar phases, and wind stealth factors.
 *
 * Fields:
 * - pressureAnalyzer: Analyzes atmospheric pressure data for weather forecasting.
 * - waterThermalAnalyzer: Analyzes water temperature and hydrological data for forecasting purposes.
 * - solunarAnalyzer: Analyzes solunar activity for fishing-related insights.
 * - windStealthAnalyzer: Analyzes wind-related data, focusing on stealth impacts for angling.
 * - executor: An Executor instance used for asynchronous task execution.
 * - log: Logger instance for logging messages and system events.
 *
 * Constructors:
 * - ForecastEngine: Initializes the ForecastEngine with specific analyzers and an executor instance.
 *
 * Methods:
 * - calculate: Calculates a global forecast result asynchronously based on weather context, fish profiles, and a target time.
 * - calculateForProfileAsync: Asynchronously calculates a tactical report for a given fish profile.
 * - compileTacticalReport: Compiles a tactical fishing report for a specific species, considering environmental conditions and analysis results.
 * - buildGlobalResult: Builds a global forecast result by analyzing weather context and tactical reports.
 * - scoreRounding: Rounds a numeric score value to one decimal place.
 * - findClosest: Finds the closest element in a timeline to a given target time using a custom time extractor.
 * - generateChart: Generates a list of chart data points based on the given timeline and type, such as pressure or temperature.
 * - calculatePressureLevel: Calculates the pressure level for a specific target time, providing a string result.
 */
@Slf4j
@Service
public class ForecastEngine {

    private final PressureAnalyzer pressureAnalyzer;
    private final WaterThermalAnalyzer waterThermalAnalyzer;
    private final SolunarAnalyzer solunarAnalyzer;
    private final WindStealthAnalyzer windStealthAnalyzer;
    private final Executor executor;

    public ForecastEngine(
            PressureAnalyzer pressureAnalyzer,
            WaterThermalAnalyzer waterThermalAnalyzer,
            SolunarAnalyzer solunarAnalyzer,
            WindStealthAnalyzer windStealthAnalyzer,
            @Qualifier("dataFetchExecutor") Executor executor) {
        this.pressureAnalyzer = pressureAnalyzer;
        this.waterThermalAnalyzer = waterThermalAnalyzer;
        this.solunarAnalyzer = solunarAnalyzer;
        this.windStealthAnalyzer = windStealthAnalyzer;
        this.executor = executor;
    }

    /**
     * Calculates a global forecast result based on the provided weather context, fish profiles, and target time.
     *
     * @param context The weather context containing environmental and situational data for the calculation.
     * @param profiles A list of fish profiles, each representing species-specific information required for the forecast.
     * @param targetTime The target date and time for which the forecast is being calculated.
     * @return A CompletableFuture containing the calculated global forecast result.
     */
    public CompletableFuture<GlobalForecastResult> calculate(WeatherContext context, List<FishProfile> profiles, LocalDateTime targetTime) {
        log.info("Inicjalizacja silnika telemetrycznego dla {} profili.", profiles.size());

        List<CompletableFuture<SpeciesTacticalReport>> reportFutures = profiles.stream()
                .map(profile -> calculateForProfileAsync(context, profile, targetTime))
                .toList();

        return CompletableFuture.allOf(reportFutures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<SpeciesTacticalReport> reports = reportFutures.stream()
                            .map(CompletableFuture::join)
                            .toList();

                    return buildGlobalResult(context, reports, targetTime);
                });
    }

    /**
     * Asynchronously calculates a tactical report for a given fish profile based on weather conditions and other factors.
     *
     * @param context the weather context containing environmental data used for analysis
     * @param profile the fish profile for which the tactical report is to be generated
     * @param targetTime the specific time target for the report calculation
     * @return a CompletableFuture that resolves to a SpeciesTacticalReport containing the results of the analyses
     */
    private CompletableFuture<SpeciesTacticalReport> calculateForProfileAsync(WeatherContext context, FishProfile profile, LocalDateTime targetTime) {
        CompletableFuture<AnalyzerResult> pFuture = CompletableFuture.supplyAsync(() -> pressureAnalyzer.analyze(context, profile, targetTime), executor);
        CompletableFuture<AnalyzerResult> wFuture = CompletableFuture.supplyAsync(() -> waterThermalAnalyzer.analyze(context, profile, targetTime), executor);
        CompletableFuture<AnalyzerResult> sFuture = CompletableFuture.supplyAsync(() -> solunarAnalyzer.analyze(context, profile, targetTime), executor);
        CompletableFuture<AnalyzerResult> stFuture = CompletableFuture.supplyAsync(() -> windStealthAnalyzer.analyze(context, profile, targetTime), executor);

        return CompletableFuture.allOf(pFuture, wFuture, sFuture, stFuture).thenApply(v ->
                compileTacticalReport(profile, context, targetTime, pFuture.join(), wFuture.join(), sFuture.join(), stFuture.join())
        );
    }

    /**
     * Compiles a tactical fishing report for a specific species based on various environmental
     * and analytical parameters, including weather, hydrological conditions, and heuristic
     * analysis results.
     *
     * @param profile the fish profile containing species-specific characteristics.
     * @param context the weather context providing environmental data such as synoptic and hydrological timelines.
     * @param targetTime the specific time for which the tactical report is being generated.
     * @param pRes the pressure-related analyzer result.
     * @param wRes the thermal and hydro-related analyzer result.
     * @param sRes the solunar-related analyzer result.
     * @param stRes the wind stealth-related analyzer result.
     * @return a compiled tactical fishing report that includes species-specific recommendations,
     *         environmental conditions, calculated scores, and suggested fishing strategies.
     */
    private SpeciesTacticalReport compileTacticalReport(
            FishProfile profile, WeatherContext context, LocalDateTime targetTime,
            AnalyzerResult pRes, AnalyzerResult wRes, AnalyzerResult sRes, AnalyzerResult stRes) {

        double totalWeight = pRes.weight() + wRes.weight() + sRes.weight() + stRes.weight();
        double totalScore = totalWeight > 0 ?
                ((pRes.score() * pRes.weight()) + (wRes.score() * wRes.weight()) + (sRes.score() * sRes.weight()) + (stRes.score() * stRes.weight())) / totalWeight
                : 50.0;

        SynopSnapshot synop = findClosest(context.synopTimeline(), targetTime, SynopSnapshot::timestamp);
        HydroSnapshot hydro = findClosest(context.hydroTimeline(), targetTime, HydroSnapshot::timestamp);

        double tempW = (hydro != null && hydro.waterTemperature() != null) ? hydro.waterTemperature().doubleValue() : 15.0;
        double windKmh = (synop != null && synop.windSpeed() != null) ? synop.windSpeed().doubleValue() : 10.0;
        int clouds = (synop != null && synop.cloudCover() != null) ? synop.cloudCover() : 50;
        double discharge = (hydro != null && hydro.discharge() != null) ? hydro.discharge().doubleValue() : 0.0;

        String preySize = "Średni (7-12cm)";
        String metabolism = "Umiarkowany (Steady)";
        Month month = targetTime.getMonth();

        if (tempW < 8) {
            preySize = (month == Month.NOVEMBER || month == Month.DECEMBER) ? "MAKSYMALNY (15-25cm) - Hyperphagia" : "MIKRO (3-5cm) - Oszczędzanie energii";
            metabolism = "LENIWY (Wymagane pauzy 3-5s)";
        } else if (tempW > 22) {
            metabolism = "STRES TERMICZNY (Żerowanie tylko w oknach tlenowych)";
        } else if (month == Month.MAY || month == Month.JUNE) {
            preySize = "NARYBEK (Wylęg 3-7cm)";
            metabolism = "AGRESYWNY (Szybkie prowadzenie)";
        }

        String penetration = (clouds > 70 || discharge > 50) ? "NISKA (Mętna woda/Ciemno)" : "WYSOKA (Clear Water)";
        List<String> colors = new ArrayList<>();
        if (clouds > 60 || discharge > 30) {
            colors.addAll(List.of("Fluo Żółty", "Firetiger", "Biały Perłowy", "Seledyn"));
        } else {
            colors.addAll(List.of("Naturalna Płoć", "Oliwka", "Srebro/Ghost", "MotorOil (UV)"));
        }

        String acoustics = (windKmh > 20) ? "Głośne grzechotki (Rattling) - Maskowanie przez fale" : "Ciche (Silent) - Ryba płochliwa";
        String ballistics = (windKmh > 25) ? "Ciężkie główki 15g+ (Przebijanie wiatru)" : "Lekki zestaw (Finezyjny opad)";
        String zAxis = (pRes.score() > 70) ? "Płycizny / Blaty / Tuż pod powierzchnią" : "Głębokie rynny / Stoki / Przy dnie";

        List<String> combinedTips = new ArrayList<>();
        combinedTips.addAll(pRes.tackleTips());
        combinedTips.addAll(wRes.tackleTips());
        combinedTips.addAll(sRes.tackleTips());
        combinedTips.addAll(stRes.tackleTips());

        return SpeciesTacticalReport.builder()
                .speciesId(profile.id())
                .speciesName(profile.name())
                .totalScore(scoreRounding(totalScore))
                .zAxisLocation(zAxis)
                .migrationVector(pRes.dominantFactor().contains("Spadek") ? "Aktywny ruch w stronę brzegu" : "Trzymanie się głębokich kryjówek")
                .preySize(preySize)
                .metabolismState(metabolism)
                .waterPenetration(penetration)
                .suggestedColors(colors)
                .acoustics(acoustics)
                .tackleBallistics(ballistics)
                .pressureScore(scoreRounding(pRes.score()))
                .thermalHydroScore(scoreRounding(wRes.score()))
                .solunarScore(scoreRounding(sRes.score()))
                .windStealthScore(scoreRounding(stRes.score()))
                .combinedTips(combinedTips)
                .solunarTips(sRes.tackleTips())
                .windTips(stRes.tackleTips())
                .build();
    }

    /**
     * Builds a comprehensive global forecast result based on the provided weather context, species tactical reports,
     * and target time. The method analyzes various timelines (synoptic, hydrological, and moon data) to generate
     * forecasts, trend analyses, and angling-related insights.
     *
     * @param context   the weather context containing timelines for synoptic, hydrological, and moon data
     * @param reports   a list of species tactical reports containing data for aggregating bite indices and scores
     * @param targetTime the target time used to derive relevant snapshots and forecasts
     * @return a {@code GlobalForecastResult} object containing data such as weather forecasts, water level trends,
     *         bite indices, pressure levels, and other general information useful for angling
     */
    private GlobalForecastResult buildGlobalResult(WeatherContext context, List<SpeciesTacticalReport> reports, LocalDateTime targetTime) {
        SynopSnapshot currentSynop = findClosest(context.synopTimeline(), targetTime, SynopSnapshot::timestamp);
        HydroSnapshot currentHydro = findClosest(context.hydroTimeline(), targetTime, HydroSnapshot::timestamp);

        MoonSnapshot currentMoon = context.moonTimeline().stream()
                .filter(m -> m.date().equals(targetTime.toLocalDate()))
                .findFirst().orElse(null);

        List<MoonSnapshot> moon3d = context.moonTimeline().stream()
                .filter(m -> !m.date().isBefore(targetTime.toLocalDate()))
                .limit(3).toList();

        List<SynopSnapshot> forecast24h = context.synopTimeline().stream()
                .filter(s -> s.timestamp().isAfter(targetTime) && s.timestamp().isBefore(targetTime.plusHours(25)))
                .toList();

        double avgScore = reports.stream().mapToDouble(SpeciesTacticalReport::totalScore).average().orElse(50.0);

        String dynamicTrend = "Brak danych o wahaniach";
        if (currentHydro != null && currentHydro.waterLevel() != null && context.hydroTimeline() != null) {
            HydroSnapshot pastHydro = findClosest(context.hydroTimeline(), targetTime.minusHours(24), HydroSnapshot::timestamp);
            if (pastHydro != null && pastHydro.waterLevel() != null) {
                int deltaH = currentHydro.waterLevel() - pastHydro.waterLevel();
                if (deltaH >= 5) dynamicTrend = "Rosnący (+" + deltaH + " cm / 24h)";
                else if (deltaH <= -5) dynamicTrend = "Opadający (" + deltaH + " cm / 24h)";
                else dynamicTrend = "Stabilny (" + (deltaH > 0 ? "+" : "") + deltaH + " cm / 24h)";
            }
        }

        return GlobalForecastResult.builder()
                .currentSynop(currentSynop)
                .currentHydro(currentHydro)
                .currentMoonData(currentMoon)
                .forecast24h(forecast24h)
                .moonForecast3d(moon3d)
                .pressureChart(generateChart(context.synopTimeline(), "pressure"))
                .airTempChart(generateChart(context.synopTimeline(), "temp"))
                .windChart(generateChart(context.synopTimeline(), "wind"))
                .generalBiteIndex(scoreRounding(avgScore))
                .anglingPressureLevel(calculatePressureLevel(targetTime))
                .waterLevelTrend(dynamicTrend)
                .speciesReports(reports)
                .fullContext(context)
                .build();
    }

    /**
     * Rounds a given score value to one decimal place.
     *
     * @param value the input score value to be rounded
     * @return the score value rounded to one decimal place
     */
    private double scoreRounding(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    /**
     * Finds the closest element in the provided timeline list to the specified target time.
     * The time associated with each element is extracted using the provided timeExtractor function.
     *
     * @param <T>           the type of elements in the timeline list
     * @param timeline      the list of elements from which the closest one is to be found
     * @param target        the target time to which the closest element is to be determined
     * @param timeExtractor a function to extract the LocalDateTime value from an element in the timeline
     * @return the element from the timeline that is closest to the target time, or null if the timeline is null or empty
     */
    private <T> T findClosest(List<T> timeline, LocalDateTime target, Function<T, LocalDateTime> timeExtractor) {
        if (timeline == null || timeline.isEmpty()) {
            return null;
        }
        return timeline.stream()
                .min(Comparator.comparingLong(item ->
                        Math.abs(java.time.Duration.between(timeExtractor.apply(item), target).toMinutes())
                ))
                .orElse(null);
    }

    /**
     * Generates a list of chart data points based on the given timeline and type.
     * The method filters and maps the data from the provided timeline according
     * to the specified type, which can be either "pressure" or "temp".
     *
     * @param timeline the list of SynopSnapshot objects representing the timeline data;
     *                 must not be null but can be empty.
     * @param type the type of data to generate the chart for;
     *             valid values are "pressure" or "temp".
     * @return a list of ChartDataPoint containing the processed data for the given type.
     *         If the timeline is null, an empty list is returned.
     */
    private List<ChartDataPoint> generateChart(List<SynopSnapshot> timeline, String type) {
        if (timeline == null) return List.of();
        return timeline.stream()
                .filter(s -> {
                    if ("pressure".equals(type)) return s.pressure() != null;
                    if ("temp".equals(type)) return s.temperature() != null;
                    return false;
                })
                .map(s -> new ChartDataPoint(
                        s.timestamp(),
                        "pressure".equals(type) ? s.pressure().doubleValue() : s.temperature().doubleValue()
                ))
                .toList();
    }


    /**
     * Calculates the pressure level based on the provided target time.
     *
     * @param targetTime the LocalDateTime for which the pressure level is to be calculated
     * @return a string indicating the pressure level; "WYSOKA (Weekend)" for weekends or Friday after 3 PM,
     *         and "NISKA (Dzień roboczy)" for other times
     */
    private String calculatePressureLevel(LocalDateTime targetTime) {
        DayOfWeek day = targetTime.getDayOfWeek();
        int hour = targetTime.getHour();

        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY || (day == DayOfWeek.FRIDAY && hour >= 15)) {
            return "WYSOKA (Weekend)";
        }
        return "NISKA (Dzień roboczy)";
    }
}