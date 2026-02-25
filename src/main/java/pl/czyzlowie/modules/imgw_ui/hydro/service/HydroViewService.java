package pl.czyzlowie.modules.imgw_ui.hydro.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.imgw_api.entity.ImgwHydroData;
import pl.czyzlowie.modules.imgw_api.repository.ImgwHydroDataRepository;
import pl.czyzlowie.modules.imgw_ui.hydro.dto.HydroDashboardDto;
import pl.czyzlowie.modules.imgw_ui.hydro.dto.HydroReadingDto;
import pl.czyzlowie.modules.imgw_ui.hydro.mapper.HydroDataMapper;
import pl.czyzlowie.modules.location.enums.StationCategory;
import pl.czyzlowie.modules.location.service.LocationFinderService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HydroViewService {

    private final LocationFinderService locationFinderService;
    private final ImgwHydroDataRepository hydroDataRepo;
    private final HydroDataMapper mapper;

    public HydroDashboardDto getDashboardData(Double lat, Double lon, String locationName) {
        LocationFinderService.NearestStation nearest = locationFinderService.findNearestStation(lat, lon, StationCategory.HYDRO);

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(5);

        List<ImgwHydroData> rawData = hydroDataRepo.findByStationIdAndWaterLevelDateBetweenOrderByWaterLevelDateAsc(
                nearest.stationId(), startDate, endDate);

        String stationName = (!rawData.isEmpty() && rawData.get(0).getStation() != null)
                ? rawData.get(0).getStation().getName() : "Nieznana stacja";

        List<HydroReadingDto> readings = rawData.stream()
                .map(mapper::mapHydro)
                .filter(r -> r.getTimestamp() != null)
                .toList();

        if (readings.isEmpty()) {
            return buildEmptyDashboard(locationName, nearest.stationId(), stationName, nearest.distanceKm());
        }

        return buildDashboard(readings, locationName, nearest.stationId(), stationName, nearest.distanceKm());
    }

    private HydroDashboardDto buildDashboard(List<HydroReadingDto> readings, String locationName, String stationId, String stationName, double distanceKm) {
        HydroReadingDto current = readings.get(readings.size() - 1);
        HydroDashboardDto.Trend trend = calculateTrend(readings);

        boolean hasLevel = readings.stream().anyMatch(r -> r.getWaterLevel() != null);
        boolean hasDischarge = readings.stream().anyMatch(r -> r.getDischarge() != null);
        boolean hasTemp = readings.stream().anyMatch(r -> r.getWaterTemperature() != null);

        return HydroDashboardDto.builder()
                .locationName(locationName)
                .stationId(stationId)
                .stationName(stationName)
                .distanceKm(Math.round(distanceKm * 10.0) / 10.0)
                .currentReading(current)
                .waterLevelTrend(trend)
                .history(readings)

                .lastWaterLevelTime(getLastValidTime(readings, "LEVEL"))
                .lastDischargeTime(getLastValidTime(readings, "DISCHARGE"))
                .lastTemperatureTime(getLastValidTime(readings, "TEMP"))
                .waterLevelStale(isDataStaleOrFlatlined(readings.stream().map(r -> r.getWaterLevel() != null ? r.getWaterLevel().doubleValue() : null).toList()))
                .dischargeStale(isDataStaleOrFlatlined(readings.stream().map(r -> r.getDischarge() != null ? r.getDischarge().doubleValue() : null).toList()))
                .temperatureStale(isDataStaleOrFlatlined(readings.stream().map(r -> r.getWaterTemperature() != null ? r.getWaterTemperature().doubleValue() : null).toList()))

                .chartLabels(readings.stream().map(HydroReadingDto::getTimeLabel).toList())
                .chartTimestampsIso(readings.stream().map(HydroReadingDto::getTimestampIso).toList())
                .chartWaterLevels(readings.stream().map(HydroReadingDto::getWaterLevel).toList())
                .chartDischarges(readings.stream().map(HydroReadingDto::getDischarge).toList())
                .chartWaterTemperatures(readings.stream().map(HydroReadingDto::getWaterTemperature).toList())
                .hasWaterLevelData(hasLevel)
                .hasDischargeData(hasDischarge)
                .hasTemperatureData(hasTemp)
                .build();
    }

    private String getLastValidTime(List<HydroReadingDto> readings, String type) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM HH:mm");
        for (int i = readings.size() - 1; i >= 0; i--) {
            HydroReadingDto r = readings.get(i);
            if (type.equals("LEVEL") && r.getWaterLevel() != null && r.getWaterLevelDate() != null) return r.getWaterLevelDate().format(fmt);
            if (type.equals("DISCHARGE") && r.getDischarge() != null && r.getDischargeDate() != null) return r.getDischargeDate().format(fmt);
            if (type.equals("TEMP") && r.getWaterTemperature() != null && r.getWaterTemperatureDate() != null) return r.getWaterTemperatureDate().format(fmt);
        }
        return "Brak danych";
    }

    private boolean isDataStaleOrFlatlined(List<Double> values) {
        List<Double> nonNulls = values.stream().filter(Objects::nonNull).toList();
        if (nonNulls.size() < 10) return false;
        List<Double> last10 = nonNulls.subList(nonNulls.size() - 10, nonNulls.size());
        double firstOfLast10 = last10.get(0);
        return last10.stream().allMatch(v -> v == firstOfLast10);
    }

    private HydroDashboardDto.Trend calculateTrend(List<HydroReadingDto> readings) {
        if (readings.size() < 2) return HydroDashboardDto.Trend.UNKNOWN;
        HydroReadingDto latest = readings.get(readings.size() - 1);
        if (latest.getWaterLevel() == null) return HydroDashboardDto.Trend.UNKNOWN;

        LocalDateTime targetTime = latest.getTimestamp().minusHours(24);
        HydroReadingDto pastReading = readings.stream()
                .filter(r -> r.getWaterLevel() != null && r.getTimestamp().isBefore(targetTime))
                .max((r1, r2) -> r1.getTimestamp().compareTo(r2.getTimestamp()))
                .orElse(readings.get(0));

        if (pastReading.getWaterLevel() == null) return HydroDashboardDto.Trend.UNKNOWN;
        int diff = latest.getWaterLevel() - pastReading.getWaterLevel();

        if (diff >= 5) return HydroDashboardDto.Trend.RISING;
        if (diff <= -5) return HydroDashboardDto.Trend.FALLING;
        return HydroDashboardDto.Trend.STABLE;
    }

    private HydroDashboardDto buildEmptyDashboard(String locationName, String stationId, String stationName, double distanceKm) {
        return HydroDashboardDto.builder()
                .locationName(locationName)
                .stationId(stationId)
                .stationName(stationName)
                .distanceKm(Math.round(distanceKm * 10.0) / 10.0)
                .currentReading(HydroReadingDto.builder().build())
                .waterLevelTrend(HydroDashboardDto.Trend.UNKNOWN)
                .history(List.of())
                .chartLabels(List.of())
                .chartTimestampsIso(List.of())
                .chartWaterLevels(List.of())
                .chartDischarges(List.of())
                .chartWaterTemperatures(List.of())
                .hasWaterLevelData(false).hasDischargeData(false).hasTemperatureData(false)
                .waterLevelStale(false).dischargeStale(false).temperatureStale(false)
                .build();
    }
}