package pl.czyzlowie.modules.barometer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.barometer.dto.BarometerChartData;
import pl.czyzlowie.modules.barometer.dto.ForecastPressurePoint;
import pl.czyzlowie.modules.barometer.dto.PressurePoint;
import pl.czyzlowie.modules.barometer.entity.PressureTrend;
import pl.czyzlowie.modules.barometer.entity.StationBarometerId;
import pl.czyzlowie.modules.barometer.entity.StationBarometerStats;
import pl.czyzlowie.modules.barometer.entity.StationType;
import pl.czyzlowie.modules.barometer.repository.StationBarometerStatsRepository;
import pl.czyzlowie.modules.forecast.repository.VirtualStationDataRepository;
import pl.czyzlowie.modules.forecast.repository.WeatherForecastRepository;
import pl.czyzlowie.modules.imgw_api.repository.ImgwSynopDataRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

/**
 * The BarometerEngineService class provides functionality to analyze barometric data from various stations,
 * calculate statistical trends, detect significant changes like approaching fronts, and manage data persistence.
 * It integrates with various repositories for historical and forecast pressure data and works with both
 * real-world and virtual station data sources.
 *
 * This service includes methods for calculating pressure statistics, building timelines from data points,
 * detecting weather trends, and evaluating the barometric stability index. It also constructs detailed chart
 * data for visualization purposes.
 *
 * The key features provided by this service include:
 * - Calculation of short-term and long-term pressure change trends.
 * - Detection of significant pressure drops to identify approaching weather fronts.
 * - Building structured timelines for historical and forecast data points.
 * - Generating data necessary for barometric chart visualizations.
 * - Integration with repositories for data retrieval and persistence.
 *
 * Dependencies:
 * - StationBarometerStatsRepository: Repository for persisting calculated barometer statistics.
 * - WeatherForecastRepository: Repository for accessing forecasted pressure data.
 * - ImgwSynopDataRepository: Repository for accessing historical pressure data from IMGW station sources.
 * - VirtualStationDataRepository: Repository for accessing virtual station pressure data.
 *
 * Constants:
 * - HISTORY_WINDOW_HOURS: Defines the number of hours in the past to consider for historical data analysis.
 * - FRONT_WINDOW_HOURS: Defines the forecasted time window to analyze for significant front detection.
 * - FRONT_DROP_THRESHOLD: The minimum pressure drop threshold for detecting a weather front approach.
 * - DEFAULT_PRESSURE: Default pressure value for cases where no data is available.
 * - TREND_FAST_THRESHOLD: Threshold for determining rapid pressure changes.
 * - TREND_NORMAL_THRESHOLD: Threshold for determining moderate pressure changes.
 *
 * Methods:
 * - calculateAndSaveStats(): Computes and persists pressure statistics for a given station and station type.
 * - fetchStationData(): Retrieves historical and forecast pressure data for a specific station based on its type.
 * - buildTimeline(): Converts data points into a structured timeline using provided time and value extractors.
 * - detectApproachingFront(): Detects significant pressure drops over a forecasted time window.
 * - buildChartData(): Compiles data into chart-ready structures for historical and forecast pressure trends.
 * - extractSeries(): Extracts pressure data for a specified time range into a chart-compatible format.
 * - getClosestPressure(): Finds the pressure value closest to a target time point in the data timeline.
 * - calculateDelta(): Computes the pressure change between two data points.
 * - determineTrend(): Evaluates the trend (e.g., rising, falling, stable) based on a pressure change delta.
 * - calculateStabilityIndex(): Determines a stability index based on pressure variations over a time period.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BarometerEngineService {

    private static final int HISTORY_WINDOW_HOURS = 132;
    private static final int FRONT_WINDOW_HOURS = 48;

    private static final BigDecimal FRONT_DROP_THRESHOLD = BigDecimal.valueOf(7.0);
    private static final BigDecimal DEFAULT_PRESSURE = BigDecimal.valueOf(1013);

    private static final double TREND_FAST_THRESHOLD = 2.0;
    private static final double TREND_NORMAL_THRESHOLD = 0.5;

    private final StationBarometerStatsRepository statsRepository;
    private final WeatherForecastRepository forecastRepository;
    private final ImgwSynopDataRepository imgwDataRepository;
    private final VirtualStationDataRepository virtualDataRepository;

    /**
     * Calculates statistical data for a given station based on its historical and forecasted pressure data,
     * updates the station's barometer statistics, and persists the updated statistics in the repository.
     *
     * @param stationId  the unique identifier of the station for which statistics are to be calculated
     * @param type       the type of the station (e.g., atmospheric station type)
     */
    @Transactional
    public void calculateAndSaveStats(String stationId, StationType type) {
        var now = LocalDateTime.now();
        var since = now.minusHours(HISTORY_WINDOW_HOURS);

        var stationData = fetchStationData(stationId, type, now, since);
        var historyTimeline = stationData.history();

        if (historyTimeline.isEmpty()) {
            log.warn("Brak danych historycznych w dedykowanych tabelach dla stacji {}. Przerywam.", stationId);
            return;
        }

        var latestMeasurementTime = historyTimeline.lastKey();
        var currentPressure = historyTimeline.get(latestMeasurementTime);
        var forecastTimeline = buildTimeline(stationData.forecast(), ForecastPressurePoint::getForecastTime, ForecastPressurePoint::getPressure);
        var p24 = getClosestPressure(historyTimeline, latestMeasurementTime.minusHours(24));
        var p72 = getClosestPressure(historyTimeline, latestMeasurementTime.minusHours(72));
        var p120 = getClosestPressure(historyTimeline, latestMeasurementTime.minusHours(120));
        var delta24h = calculateDelta(currentPressure, p24);

        var statsId = new StationBarometerId(stationId, type);
        var stats = statsRepository.findById(statsId)
                .orElseGet(() -> StationBarometerStats.builder().id(statsId).build());

        stats.setCurrentPressure(currentPressure);
        stats.setDelta24h(delta24h);
        stats.setDelta3d(calculateDelta(currentPressure, p72));
        stats.setDelta5d(calculateDelta(currentPressure, p120));
        stats.setTrend24h(determineTrend(delta24h));
        stats.setPressureStabilityIndex(calculateStabilityIndex(historyTimeline, latestMeasurementTime));
        stats.setChartData(buildChartData(historyTimeline, forecastTimeline, latestMeasurementTime));
        stats.setFrontApproaching(detectApproachingFront(currentPressure, forecastTimeline, latestMeasurementTime));
        stats.setLastUpdatedAt(now);

        statsRepository.save(stats);
    }


    /**
     * Fetches the station data for the specified station, type, and time range.
     *
     * @param stationId the unique identifier of the station
     * @param type the type of the station (e.g., IMGW_SYNOP, Virtual)
     * @param now the current date and time
     * @param since the start date and time for fetching historical data
     * @return a {@code StationData} object containing the history timeline and forecast data
     */
    private StationData fetchStationData(String stationId, StationType type, LocalDateTime now, LocalDateTime since) {
        return switch (type) {
            case IMGW_SYNOP -> {
                var rawHistory = imgwDataRepository.findPressureHistory(stationId, since.toLocalDate());
                var historyTimeline = buildTimeline(rawHistory,
                        p -> p.getMeasurementDate().atTime(p.getMeasurementHour(), 0),
                        PressurePoint::getPressure);
                var rawForecast = forecastRepository.findPressureForecast(stationId, now);
                yield new StationData(historyTimeline, rawForecast);
            }
            default -> { // Virtual
                var rawHistory = virtualDataRepository.findPressureHistory(stationId, since, now);
                var historyTimeline = buildTimeline(rawHistory, ForecastPressurePoint::getForecastTime, ForecastPressurePoint::getPressure);
                var rawForecast = forecastRepository.findVirtualPressureForecast(stationId, now);
                yield new StationData(historyTimeline, rawForecast);
            }
        };
    }

    /**
     * Builds a timeline by extracting timestamps and associated values from a list of points.
     *
     * @param <T> the type of elements in the list
     * @param points the list of points to process
     * @param timeExtractor a function to extract the timestamp from each point
     * @param valueExtractor a function to extract the value associated with the timestamp from each point
     * @return a TreeMap where keys are timestamps and values are the corresponding extracted values
     */
    private <T> TreeMap<LocalDateTime, BigDecimal> buildTimeline(List<T> points,
                                                                 Function<T, LocalDateTime> timeExtractor,
                                                                 Function<T, BigDecimal> valueExtractor) {
        var map = new TreeMap<LocalDateTime, BigDecimal>();
        for (var point : points) {
            map.put(timeExtractor.apply(point), valueExtractor.apply(point));
        }
        return map;
    }

    /**
     * Detects if there is an approaching weather front based on the current pressure and the pressure forecast.
     *
     * @param currentPressure the current atmospheric pressure
     * @param forecast a TreeMap containing forecasted pressure values keyed by their corresponding LocalDateTime
     * @param now the current timestamp to determine the relevant forecast window
     * @return true if there is an approaching front indicated by a pressure drop that meets or exceeds the threshold; false otherwise
     */
    private boolean detectApproachingFront(BigDecimal currentPressure, TreeMap<LocalDateTime, BigDecimal> forecast, LocalDateTime now) {
        var next48h = forecast.headMap(now.plusHours(FRONT_WINDOW_HOURS), true);
        if (next48h.isEmpty()) return false;

        var highestInForecast = currentPressure;
        var maxDrop = BigDecimal.ZERO;

        for (var futurePressure : next48h.values()) {
            if (futurePressure.compareTo(highestInForecast) > 0) {
                highestInForecast = futurePressure;
            } else {
                var currentDrop = highestInForecast.subtract(futurePressure);
                if (currentDrop.compareTo(maxDrop) > 0) {
                    maxDrop = currentDrop;
                }
            }
        }
        return maxDrop.compareTo(FRONT_DROP_THRESHOLD) >= 0;
    }

    /**
     * Builds a BarometerChartData object containing historical and forecasted data series
     * for specific time intervals relative to the current time.
     *
     * @param history a TreeMap containing historical data points, where the keys represent
     *                LocalDateTime timestamps and the values represent corresponding measurements.
     * @param forecast a TreeMap containing forecasted data points, where the keys represent
     *                 LocalDateTime timestamps and the values represent corresponding predicted values.
     * @param now the current LocalDateTime used to determine the bounds for extracting
     *            historical and forecasted data series.
     * @return a BarometerChartData object populated with historical and forecasted data series
     *         based on the specified time intervals.
     */
    private BarometerChartData buildChartData(TreeMap<LocalDateTime, BigDecimal> history,
                                              TreeMap<LocalDateTime, BigDecimal> forecast,
                                              LocalDateTime now) {
        return BarometerChartData.builder()
                .history24h(extractSeries(history, now.minusHours(24), now))
                .history3d(extractSeries(history, now.minusHours(72), now))
                .history5d(extractSeries(history, now.minusHours(120), now))
                .forecast24h(extractSeries(forecast, now, now.plusHours(24)))
                .forecast3d(extractSeries(forecast, now, now.plusHours(72)))
                .build();
    }

    /**
     * Extracts a series of data points from a given timeline within the specified
     * start and end date-time range. The data points are represented by the
     * matching date-time and corresponding value.
     *
     * @param timeline a TreeMap containing LocalDateTime keys and BigDecimal values
     *                 representing the data points in the timeline
     * @param start the starting LocalDateTime of the range; inclusive
     * @param end the ending LocalDateTime of the range; inclusive
     * @return a list of DataPoint objects representing the extracted series
     *         within the specified date-time range
     */
    private List<BarometerChartData.DataPoint> extractSeries(TreeMap<LocalDateTime, BigDecimal> timeline,
                                                             LocalDateTime start,
                                                             LocalDateTime end) {
        return timeline.subMap(start, true, end, true)
                .entrySet()
                .stream()
                .map(e -> new BarometerChartData.DataPoint(e.getKey().toString(), e.getValue()))
                .toList();
    }

    /**
     * Retrieves the closest pressure value from a timeline based on the given target timestamp.
     * If there is no exact match, the method determines the closest timestamp by comparing
     * the distances of the surrounding entries.
     *
     * @param timeline a TreeMap containing timestamped pressure values, where the keys are
     *                 LocalDateTime instances representing timestamps and the values are
     *                 BigDecimal instances representing corresponding pressure readings
     * @param target   the target LocalDateTime to find the closest pressure value for
     * @return the closest pressure value as a BigDecimal. If the timeline is empty or there
     *         are no timestamps around the target, returns null.
     */
    private BigDecimal getClosestPressure(TreeMap<LocalDateTime, BigDecimal> timeline, LocalDateTime target) {
        var floor = timeline.floorKey(target);
        var ceiling = timeline.ceilingKey(target);

        if (floor == null && ceiling == null) return null;
        if (floor == null) return timeline.get(ceiling);
        if (ceiling == null) return timeline.get(floor);

        var diffFloor = Math.abs(Duration.between(floor, target).toMinutes());
        var diffCeiling = Math.abs(Duration.between(ceiling, target).toMinutes());

        return (diffFloor <= diffCeiling) ? timeline.get(floor) : timeline.get(ceiling);
    }

    /**
     * Calculates the delta (difference) between the current and past values, rounded to one decimal place.
     * If either value is null, the method returns BigDecimal.ZERO.
     *
     * @param current the current value, represented as a BigDecimal
     * @param past the past value, represented as a BigDecimal
     * @return the calculated delta as a BigDecimal rounded to one decimal place, or BigDecimal.ZERO if any input is null
     */
    private BigDecimal calculateDelta(BigDecimal current, BigDecimal past) {
        if (past == null || current == null) return BigDecimal.ZERO;
        return current.subtract(past).setScale(1, RoundingMode.HALF_UP);
    }

    /**
     * Determines the pressure trend based on the given delta value.
     *
     * @param delta the change in pressure as a BigDecimal, representing the difference
     *              in pressure over time
     * @return the determined PressureTrend, which could be one of RISING_FAST, RISING,
     *         FALLING_FAST, FALLING, or STABLE, based on the magnitude and direction
     *         of the pressure delta
     */
    private PressureTrend determineTrend(BigDecimal delta) {
        double val = delta.doubleValue();
        if (val >= TREND_FAST_THRESHOLD) return PressureTrend.RISING_FAST;
        if (val >= TREND_NORMAL_THRESHOLD) return PressureTrend.RISING;
        if (val <= -TREND_FAST_THRESHOLD) return PressureTrend.FALLING_FAST;
        if (val <= -TREND_NORMAL_THRESHOLD) return PressureTrend.FALLING;
        return PressureTrend.STABLE;
    }

    /**
     * Calculates a stability index based on pressure differences within a specified timeline.
     * The stability index is a measure ranging from 0 to 100, with 100 indicating maximum stability
     * and 0 indicating minimal stability. The calculation considers pressure values over the last 72 hours.
     *
     * @param timeline a TreeMap containing LocalDateTime keys and corresponding BigDecimal pressure values.
     * @param now the current LocalDateTime used as a reference for calculating the 72-hour range.
     * @return an Integer representing the calculated stability index. Returns 50 if no data is available
     *         in the 72-hour range.
     */
    private Integer calculateStabilityIndex(TreeMap<LocalDateTime, BigDecimal> timeline, LocalDateTime now) {
        var values3d = timeline.tailMap(now.minusHours(72)).values();
        if (values3d.isEmpty()) return 50;

        var summary = values3d.stream().mapToDouble(BigDecimal::doubleValue).summaryStatistics();
        double max = summary.getCount() > 0 ? summary.getMax() : currentPressureFromTimeline(timeline).doubleValue();
        double min = summary.getCount() > 0 ? summary.getMin() : currentPressureFromTimeline(timeline).doubleValue();

        double difference = max - min;
        if (difference <= 2.0) return 100;
        if (difference >= 15.0) return 0;

        return (int) Math.round(100.0 - ((difference - 2.0) / 13.0) * 100.0);
    }

    /**
     * Retrieves the current pressure value from a timeline of pressure readings.
     *
     * @param timeline a TreeMap where keys represent timestamps (in LocalDateTime)
     *                 and values represent corresponding pressure readings (in BigDecimal).
     *                 The timeline must be in chronological order.
     * @return the most recent pressure value from the timeline.
     *         If the timeline is empty, returns the default pressure value.
     */
    private BigDecimal currentPressureFromTimeline(TreeMap<LocalDateTime, BigDecimal> timeline) {
        return timeline.isEmpty() ? DEFAULT_PRESSURE : timeline.get(timeline.lastKey());
    }

    /**
     * A record representing station data, which encapsulates pressure history and forecast information.
     *
     * This class stores a map of historical pressure data associated with specific timestamps,
     * and a list of forecasted pressure values for future time periods.
     *
     * Immutable and thread-safe due to the use of {@code TreeMap}, {@code List}, and the record construct.
     *
     * @param history a {@code TreeMap} where each key is a {@code LocalDateTime} instance representing
     *                a historical timestamp, and the corresponding value is a {@code BigDecimal} representing
     *                the recorded pressure at that time
     * @param forecast a {@code List} of {@code ForecastPressurePoint} objects representing predicted pressure
     *                 values and their related metadata for future times
     */
    private record StationData(TreeMap<LocalDateTime, BigDecimal> history, List<ForecastPressurePoint> forecast) {}
}