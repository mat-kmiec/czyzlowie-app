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
import pl.czyzlowie.modules.barometer.provider.BarometerDataProvider;
import pl.czyzlowie.modules.barometer.repository.StationBarometerStatsRepository;
import pl.czyzlowie.modules.forecast.repository.VirtualStationDataRepository;
import pl.czyzlowie.modules.forecast.repository.WeatherForecastRepository;
import pl.czyzlowie.modules.imgw.repository.ImgwSynopDataRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BarometerEngineService {

    private final StationBarometerStatsRepository statsRepository;
    private final WeatherForecastRepository forecastRepository;
    private final ImgwSynopDataRepository imgwDataRepository;
    private final VirtualStationDataRepository virtualDataRepository;

    @Transactional
    public void calculateAndSaveStats(String stationId, StationType type) {
//        log.info("Rozpoczynam obliczanie statystyk: {} ({})", stationId, type);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime since = now.minusHours(132);

        TreeMap<LocalDateTime, BigDecimal> historyTimeline;
        List<ForecastPressurePoint> rawForecast;

        if (type == StationType.IMGW_SYNOP) {
            List<PressurePoint> rawHistory = imgwDataRepository.findPressureHistory(stationId, since.toLocalDate());
            historyTimeline = buildHistoryTimelineFromImgw(rawHistory);
            rawForecast = forecastRepository.findPressureForecast(stationId, now);
        } else {
            List<ForecastPressurePoint> rawHistory = virtualDataRepository.findPressureHistory(stationId, since, now);
            historyTimeline = buildHistoryTimelineFromVirtual(rawHistory);
            rawForecast = forecastRepository.findVirtualPressureForecast(stationId, now);
        }

        if (historyTimeline.isEmpty()) {
            log.warn("Brak danych historycznych w dedykowanych tabelach dla stacji {}. Przerywam.", stationId);
            return;
        }

        LocalDateTime latestMeasurementTime = historyTimeline.lastKey();
        BigDecimal currentPressure = historyTimeline.get(latestMeasurementTime);

        TreeMap<LocalDateTime, BigDecimal> forecastTimeline = buildForecastTimeline(rawForecast);

        BigDecimal p24 = getClosestPressure(historyTimeline, latestMeasurementTime.minusHours(24));
        BigDecimal p72 = getClosestPressure(historyTimeline, latestMeasurementTime.minusHours(72));
        BigDecimal p120 = getClosestPressure(historyTimeline, latestMeasurementTime.minusHours(120));

        BarometerChartData chartData = buildChartData(historyTimeline, forecastTimeline, latestMeasurementTime);
        boolean isFrontApproaching = detectApproachingFront(currentPressure, forecastTimeline, latestMeasurementTime);

        StationBarometerId id = new StationBarometerId(stationId, type);
        StationBarometerStats stats = statsRepository.findById(id)
                .orElseGet(() -> StationBarometerStats.builder().id(id).build());

        stats.setCurrentPressure(currentPressure);
        stats.setDelta24h(calculateDelta(currentPressure, p24));
        stats.setDelta3d(calculateDelta(currentPressure, p72));
        stats.setDelta5d(calculateDelta(currentPressure, p120));
        stats.setTrend24h(determineTrend(stats.getDelta24h()));
        stats.setPressureStabilityIndex(calculateStabilityIndex(historyTimeline, latestMeasurementTime));
        stats.setChartData(chartData);
        stats.setFrontApproaching(isFrontApproaching);
        stats.setLastUpdatedAt(now);

        statsRepository.save(stats);
//        log.info("Zaktualizowano statystyki dla stacji: {} ({})", stationId, type);
    }

    private TreeMap<LocalDateTime, BigDecimal> buildHistoryTimelineFromImgw(List<PressurePoint> points) {
        TreeMap<LocalDateTime, BigDecimal> map = new TreeMap<>();
        for (PressurePoint p : points) {
            map.put(p.getMeasurementDate().atTime(p.getMeasurementHour(), 0), p.getPressure());
        }
        return map;
    }

    private TreeMap<LocalDateTime, BigDecimal> buildHistoryTimelineFromVirtual(List<ForecastPressurePoint> points) {
        TreeMap<LocalDateTime, BigDecimal> map = new TreeMap<>();
        for (ForecastPressurePoint p : points) {
            map.put(p.getForecastTime(), p.getPressure());
        }
        return map;
    }

    private TreeMap<LocalDateTime, BigDecimal> buildForecastTimeline(List<ForecastPressurePoint> points) {
        TreeMap<LocalDateTime, BigDecimal> map = new TreeMap<>();
        for (ForecastPressurePoint p : points) {
            map.put(p.getForecastTime(), p.getPressure());
        }
        return map;
    }

    private boolean detectApproachingFront(BigDecimal currentPressure, TreeMap<LocalDateTime, BigDecimal> forecast, LocalDateTime now) {
        if (forecast.isEmpty()) return false;

        LocalDateTime limit = now.plusHours(48);
        SortedMap<LocalDateTime, BigDecimal> next48h = forecast.headMap(limit, true);

        if (next48h.isEmpty()) return false;

        BigDecimal highestInForecast = currentPressure;
        BigDecimal maxDrop = BigDecimal.ZERO;

        for (BigDecimal futurePressure : next48h.values()) {
            if (futurePressure.compareTo(highestInForecast) > 0) {
                highestInForecast = futurePressure;
            } else {
                BigDecimal currentDrop = highestInForecast.subtract(futurePressure);
                if (currentDrop.compareTo(maxDrop) > 0) {
                    maxDrop = currentDrop;
                }
            }
        }
        return maxDrop.doubleValue() >= 7.0;
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

    private List<BarometerChartData.DataPoint> extractSeries(TreeMap<LocalDateTime, BigDecimal> timeline, LocalDateTime start, LocalDateTime end) {
        return timeline.subMap(start, true, end, true)
                .entrySet()
                .stream()
                .map(e -> new BarometerChartData.DataPoint(e.getKey().toString(), e.getValue()))
                .collect(Collectors.toList());
    }

    private BigDecimal getClosestPressure(TreeMap<LocalDateTime, BigDecimal> timeline, LocalDateTime target) {
        if (timeline.isEmpty()) return null;
        LocalDateTime floor = timeline.floorKey(target);
        LocalDateTime ceiling = timeline.ceilingKey(target);
        if (floor == null && ceiling == null) return null;
        if (floor == null) return timeline.get(ceiling);
        if (ceiling == null) return timeline.get(floor);
        long diffFloor = Math.abs(ChronoUnit.MINUTES.between(floor, target));
        long diffCeiling = Math.abs(ChronoUnit.MINUTES.between(ceiling, target));
        return (diffFloor <= diffCeiling) ? timeline.get(floor) : timeline.get(ceiling);
    }

    private BigDecimal calculateDelta(BigDecimal current, BigDecimal past) {
        if (past == null || current == null) return BigDecimal.ZERO;
        return current.subtract(past).setScale(1, RoundingMode.HALF_UP);
    }

    private PressureTrend determineTrend(BigDecimal delta) {
        double val = delta.doubleValue();
        if (val >= 2.0) return PressureTrend.RISING_FAST;
        if (val >= 0.5) return PressureTrend.RISING;
        if (val <= -2.0) return PressureTrend.FALLING_FAST;
        if (val <= -0.5) return PressureTrend.FALLING;
        return PressureTrend.STABLE;
    }

    private Integer calculateStabilityIndex(TreeMap<LocalDateTime, BigDecimal> timeline, LocalDateTime now) {
        LocalDateTime start3d = now.minusHours(72);
        List<BigDecimal> values3d = timeline.tailMap(start3d).values().stream().toList();
        if (values3d.isEmpty()) return 50;

        BigDecimal max = values3d.stream().max(BigDecimal::compareTo).orElse(currentPressureFromTimeline(timeline));
        BigDecimal min = values3d.stream().min(BigDecimal::compareTo).orElse(currentPressureFromTimeline(timeline));

        double difference = max.subtract(min).doubleValue();
        if (difference <= 2.0) return 100;
        if (difference >= 15.0) return 0;
        double score = 100.0 - ((difference - 2.0) / 13.0) * 100.0;
        return (int) Math.round(score);
    }

    private BigDecimal currentPressureFromTimeline(TreeMap<LocalDateTime, BigDecimal> timeline) {
        return timeline.isEmpty() ? BigDecimal.valueOf(1013) : timeline.get(timeline.lastKey());
    }
}