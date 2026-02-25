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

@Slf4j
@Service
@RequiredArgsConstructor
public class BarometerEngineService {

    private static final int HISTORY_WINDOW_HOURS = 132;
    private static final int FRONT_WINDOW_HOURS = 48;
    private static final int FRONT_SLIDING_WINDOW = 6;

    private static final BigDecimal FRONT_DROP_THRESHOLD = BigDecimal.valueOf(4.0);
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
            log.warn("[BAROMETR] Brak danych dla stacji {}. Przerywam.", stationId);
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

    private boolean detectApproachingFront(BigDecimal currentPressure, TreeMap<LocalDateTime, BigDecimal> forecast, LocalDateTime now) {
        if (forecast.isEmpty()) return false;

        var immediateFuture = forecast.headMap(now.plusHours(FRONT_SLIDING_WINDOW), true);
        for (BigDecimal futureP : immediateFuture.values()) {
            if (currentPressure.subtract(futureP).compareTo(FRONT_DROP_THRESHOLD) >= 0) {
                log.debug("[FRONT] Wykryto gwałtowny spadek względem odczytu bieżącego!");
                return true;
            }
        }

        var next48h = forecast.headMap(now.plusHours(FRONT_WINDOW_HOURS), true);
        for (var entry : next48h.entrySet()) {
            LocalDateTime startTime = entry.getKey();
            BigDecimal startP = entry.getValue();

            LocalDateTime windowEnd = startTime.plusHours(FRONT_SLIDING_WINDOW);
            var windowData = forecast.subMap(startTime, false, windowEnd, true);

            for (BigDecimal futureP : windowData.values()) {
                if (startP.subtract(futureP).compareTo(FRONT_DROP_THRESHOLD) >= 0) {
                    log.debug("[FRONT] Wykryto tąpnięcie w prognozie: {} -> {}", startP, futureP);
                    return true;
                }
            }
        }
        return false;
    }

    private Integer calculateStabilityIndex(TreeMap<LocalDateTime, BigDecimal> timeline, LocalDateTime now) {
        var values24h = timeline.tailMap(now.minusHours(24)).values();
        if (values24h.size() < 2) return 50;

        double totalVolatility = 0;
        List<BigDecimal> list = new ArrayList<>(values24h);
        for (int i = 1; i < list.size(); i++) {
            totalVolatility += Math.abs(list.get(i).doubleValue() - list.get(i-1).doubleValue());
        }

        log.debug("[STABILITY] Dobowa zmienność ciśnienia: {} hPa", String.format("%.2f", totalVolatility));

        if (totalVolatility <= 4.0) return 100;
        if (totalVolatility >= 15.0) return 0;

        return (int) Math.round(100.0 - ((totalVolatility - 4.0) / 11.0) * 100.0);
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
            default -> {
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
            BigDecimal val = valueExtractor.apply(point);
            if (val != null) {
                map.put(timeExtractor.apply(point), val);
            }
        }
        return map;
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
        if (timeline.isEmpty()) return null;
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

    private record StationData(TreeMap<LocalDateTime, BigDecimal> history, List<ForecastPressurePoint> forecast) {}
}