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
import pl.czyzlowie.modules.imgw.repository.ImgwSynopDataRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

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

    private <T> TreeMap<LocalDateTime, BigDecimal> buildTimeline(List<T> points,
                                                                 Function<T, LocalDateTime> timeExtractor,
                                                                 Function<T, BigDecimal> valueExtractor) {
        var map = new TreeMap<LocalDateTime, BigDecimal>();
        for (var point : points) {
            map.put(timeExtractor.apply(point), valueExtractor.apply(point));
        }
        return map;
    }

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

    private List<BarometerChartData.DataPoint> extractSeries(TreeMap<LocalDateTime, BigDecimal> timeline,
                                                             LocalDateTime start,
                                                             LocalDateTime end) {
        return timeline.subMap(start, true, end, true)
                .entrySet()
                .stream()
                .map(e -> new BarometerChartData.DataPoint(e.getKey().toString(), e.getValue()))
                .toList();
    }

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

    private BigDecimal calculateDelta(BigDecimal current, BigDecimal past) {
        if (past == null || current == null) return BigDecimal.ZERO;
        return current.subtract(past).setScale(1, RoundingMode.HALF_UP);
    }

    private PressureTrend determineTrend(BigDecimal delta) {
        double val = delta.doubleValue();
        if (val >= TREND_FAST_THRESHOLD) return PressureTrend.RISING_FAST;
        if (val >= TREND_NORMAL_THRESHOLD) return PressureTrend.RISING;
        if (val <= -TREND_FAST_THRESHOLD) return PressureTrend.FALLING_FAST;
        if (val <= -TREND_NORMAL_THRESHOLD) return PressureTrend.FALLING;
        return PressureTrend.STABLE;
    }

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

    private BigDecimal currentPressureFromTimeline(TreeMap<LocalDateTime, BigDecimal> timeline) {
        return timeline.isEmpty() ? DEFAULT_PRESSURE : timeline.get(timeline.lastKey());
    }

    private record StationData(TreeMap<LocalDateTime, BigDecimal> history, List<ForecastPressurePoint> forecast) {}
}