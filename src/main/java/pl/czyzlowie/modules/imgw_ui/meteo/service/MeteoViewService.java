package pl.czyzlowie.modules.imgw_ui.meteo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.imgw_api.entity.ImgwMeteoData;
import pl.czyzlowie.modules.imgw_api.repository.ImgwMeteoDataRepository;
import pl.czyzlowie.modules.imgw_ui.meteo.dto.MeteoDashboardDto;
import pl.czyzlowie.modules.imgw_ui.meteo.dto.MeteoReadingDto;
import pl.czyzlowie.modules.imgw_ui.meteo.mapper.MeteoDataMapper;
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
public class MeteoViewService {

    private final LocationFinderService locationFinderService;
    private final ImgwMeteoDataRepository meteoDataRepo;
    private final MeteoDataMapper mapper;

    public MeteoDashboardDto getDashboardData(Double lat, Double lon, String locationName) {
        LocationFinderService.NearestStation nearest = locationFinderService.findNearestStation(lat, lon, StationCategory.METEO);

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(5);

        List<ImgwMeteoData> rawData = meteoDataRepo.findByStationIdAndCreatedAtBetweenOrderByCreatedAtAsc(
                nearest.stationId(), startDate, endDate);

        String stationName = (!rawData.isEmpty() && rawData.get(0).getStation() != null && rawData.get(0).getStation().getName() != null)
                ? rawData.get(0).getStation().getName() : "Stacja Meteo";

        List<MeteoReadingDto> readings = rawData.stream()
                .map(mapper::mapMeteo)
                .filter(r -> r.getTimestamp() != null)
                .toList();

        if (readings.isEmpty()) {
            return buildEmptyDashboard(locationName, nearest.stationId(), stationName, nearest.distanceKm());
        }

        return buildDashboard(readings, locationName, nearest.stationId(), stationName, nearest.distanceKm());
    }

    private MeteoDashboardDto buildDashboard(List<MeteoReadingDto> readings, String locationName, String stationId, String stationName, double distanceKm) {
        MeteoReadingDto current = readings.get(readings.size() - 1);
        MeteoDashboardDto.Trend trend = calculateTempTrend(readings);

        boolean hasTemp = readings.stream().anyMatch(r -> r.getAirTemp() != null);
        boolean hasWind = readings.stream().anyMatch(r -> r.getWindAvgSpeed() != null);
        boolean hasPrecip = readings.stream().anyMatch(r -> r.getPrecipitation10min() != null);

        return MeteoDashboardDto.builder()
                .locationName(locationName)
                .stationId(stationId)
                .stationName(stationName)
                .distanceKm(Math.round(distanceKm * 10.0) / 10.0)
                .currentReading(current)
                .tempTrend(trend)
                .history(readings)

                .lastTempTime(getLastValidTime(readings, "TEMP"))
                .lastWindTime(getLastValidTime(readings, "WIND"))
                .lastPrecipTime(getLastValidTime(readings, "PRECIP"))

                .tempStale(isDataStale(readings.stream().map(r -> r.getAirTemp() != null ? r.getAirTemp().doubleValue() : null).toList()))
                .windStale(isDataStale(readings.stream().map(r -> r.getWindAvgSpeed() != null ? r.getWindAvgSpeed().doubleValue() : null).toList()))

                .chartLabels(readings.stream().map(MeteoReadingDto::getTimeLabel).toList())
                .chartTimestampsIso(readings.stream().map(MeteoReadingDto::getTimestampIso).toList())
                .chartAirTemps(readings.stream().map(MeteoReadingDto::getAirTemp).toList())
                .chartWindSpeeds(readings.stream().map(MeteoReadingDto::getWindAvgSpeed).toList())
                .chartWindMaxSpeeds(readings.stream().map(MeteoReadingDto::getWindMaxSpeed).toList())
                .chartPrecipitation(readings.stream().map(MeteoReadingDto::getPrecipitation10min).toList())

                .hasTempData(hasTemp).hasWindData(hasWind).hasPrecipitationData(hasPrecip)
                .build();
    }

    private String getLastValidTime(List<MeteoReadingDto> readings, String type) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM HH:mm");
        for (int i = readings.size() - 1; i >= 0; i--) {
            MeteoReadingDto r = readings.get(i);
            if (type.equals("TEMP") && r.getAirTemp() != null && r.getAirTempDate() != null) return r.getAirTempDate().format(fmt);
            if (type.equals("WIND") && r.getWindAvgSpeed() != null && r.getWindMeasurementTime() != null) return r.getWindMeasurementTime().format(fmt);
            if (type.equals("PRECIP") && r.getPrecipitation10min() != null && r.getPrecipitation10minTime() != null) return r.getPrecipitation10minTime().format(fmt);
        }
        return "Brak danych";
    }

    private boolean isDataStale(List<Double> values) {
        List<Double> nonNulls = values.stream().filter(Objects::nonNull).toList();
        if (nonNulls.size() < 12) return false;
        List<Double> last12 = nonNulls.subList(nonNulls.size() - 12, nonNulls.size());
        double first = last12.get(0);
        return last12.stream().allMatch(v -> v == first);
    }

    private MeteoDashboardDto.Trend calculateTempTrend(List<MeteoReadingDto> readings) {
        if (readings.size() < 2) return MeteoDashboardDto.Trend.UNKNOWN;
        MeteoReadingDto latest = readings.get(readings.size() - 1);
        if (latest.getAirTemp() == null) return MeteoDashboardDto.Trend.UNKNOWN;

        LocalDateTime targetTime = latest.getTimestamp().minusHours(24);
        MeteoReadingDto pastReading = readings.stream()
                .filter(r -> r.getAirTemp() != null && r.getTimestamp().isBefore(targetTime))
                .max((r1, r2) -> r1.getTimestamp().compareTo(r2.getTimestamp()))
                .orElse(readings.get(0));

        if (pastReading.getAirTemp() == null) return MeteoDashboardDto.Trend.UNKNOWN;

        double diff = latest.getAirTemp().doubleValue() - pastReading.getAirTemp().doubleValue();
        if (diff >= 3.0) return MeteoDashboardDto.Trend.RISING;
        if (diff <= -3.0) return MeteoDashboardDto.Trend.FALLING;
        return MeteoDashboardDto.Trend.STABLE;
    }

    private MeteoDashboardDto buildEmptyDashboard(String locationName, String stationId, String stationName, double distanceKm) {
        return MeteoDashboardDto.builder()
                .locationName(locationName).stationId(stationId).stationName(stationName)
                .distanceKm(Math.round(distanceKm * 10.0) / 10.0)
                .currentReading(MeteoReadingDto.builder().build())
                .tempTrend(MeteoDashboardDto.Trend.UNKNOWN)
                .history(List.of()).chartLabels(List.of()).chartTimestampsIso(List.of())
                .chartAirTemps(List.of()).chartWindSpeeds(List.of()).chartWindMaxSpeeds(List.of()).chartPrecipitation(List.of())
                .hasTempData(false).hasWindData(false).hasPrecipitationData(false)
                .build();
    }
}
