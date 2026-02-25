package pl.czyzlowie.modules.imgw_ui.synop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.forecast.entity.VirtualStationData;
import pl.czyzlowie.modules.forecast.repository.VirtualStationDataRepository;
import pl.czyzlowie.modules.imgw_api.entity.ImgwSynopData;
import pl.czyzlowie.modules.imgw_api.repository.ImgwSynopDataRepository;
import pl.czyzlowie.modules.imgw_ui.synop.dto.SynopDashboardDto;
import pl.czyzlowie.modules.imgw_ui.synop.dto.WeatherReadingDto;
import pl.czyzlowie.modules.imgw_ui.synop.mapper.WeatherDataMapper;
import pl.czyzlowie.modules.location.enums.StationCategory;
import pl.czyzlowie.modules.location.service.LocationFinderService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SynopViewService {

    private final ImgwSynopDataRepository synopDataRepo;
    private final VirtualStationDataRepository virtualDataRepo;
    private final WeatherDataMapper mapper;
    private final LocationFinderService locationFinderService;

    public SynopDashboardDto getDashboardData(Double lat, Double lon, String locationName, LocalDate date) {
        LocationFinderService.NearestStation nearest = locationFinderService.findNearestStation(lat, lon, StationCategory.SYNOPTIC);
        log.info("Do wygenerowania widoku dla {} używam stacji {} typu {}", locationName, nearest.stationId(), nearest.type());

        return switch (nearest.type()) {
            case IMGW_SYNOP -> buildFromSynop(nearest.stationId(), locationName, date, nearest.distanceKm());
            case VIRTUAL -> buildFromVirtual(nearest.stationId(), locationName, date, nearest.distanceKm());
            default -> throw new IllegalStateException("Nieobsługiwany typ stacji dla widoku Synop: " + nearest.type());
        };
    }

    private SynopDashboardDto buildFromSynop(String stationId, String locationName, LocalDate endDate, double distanceKm) {
        LocalDate startDate = endDate.minusDays(4);
        List<ImgwSynopData> rawData = synopDataRepo.findByStationIdAndMeasurementDateBetweenOrderByMeasurementDateAscMeasurementHourAsc(stationId, startDate, endDate);
        List<WeatherReadingDto> readings = rawData.stream()
                .map(mapper::mapSynop)
                .toList();

        return createDashboard(readings, locationName, "SYNOP (IMGW)", endDate, distanceKm);
    }

    private SynopDashboardDto buildFromVirtual(String stationId, String locationName, LocalDate endDate, double distanceKm) {
        LocalDateTime startOfRange = endDate.minusDays(4).atStartOfDay();
        LocalDateTime endOfRange = endDate.atTime(LocalTime.MAX);
        List<VirtualStationData> rawData = virtualDataRepo.findByVirtualStationIdAndMeasurementTimeBetweenOrderByMeasurementTimeAsc(
                stationId, startOfRange, endOfRange);

        List<WeatherReadingDto> readings = rawData.stream()
                .map(mapper::mapVirtual)
                .toList();

        return createDashboard(readings, locationName, "STACJA WIRTUALNA", endDate, distanceKm);
    }

    private SynopDashboardDto createDashboard(List<WeatherReadingDto> readings, String locationName, String type, LocalDate date, double distanceKm) {
        if (readings.isEmpty()) {
            log.warn("Brak odczytów dla stacji w lokalizacji: {}", locationName);
            return SynopDashboardDto.builder()
                    .locationName(locationName)
                    .stationType(type)
                    .distanceKm(Math.round(distanceKm * 10.0) / 10.0)
                    .selectedDate(date)
                    .dailyHistory(List.of())
                    .chartLabels(List.of())
                    .chartTemperatures(List.of())
                    .chartPressures(List.of())
                    .chartWindSpeeds(List.of())
                    .chartTimestamps(List.of())
                    .chartPrecipitation(List.of())
                    .build();
        }

        WeatherReadingDto current = readings.get(readings.size() - 1);

        return SynopDashboardDto.builder()
                .locationName(locationName)
                .stationType(type)
                .distanceKm(Math.round(distanceKm * 10.0) / 10.0)
                .selectedDate(date)
                .currentReading(current)
                .dailyHistory(readings)
                .chartLabels(readings.stream().map(WeatherReadingDto::getTimeLabel).toList())
                .chartTemperatures(readings.stream().map(WeatherReadingDto::getTemperature).toList())
                .chartPressures(readings.stream().map(WeatherReadingDto::getPressure).toList())
                .chartWindSpeeds(readings.stream().map(WeatherReadingDto::getWindSpeed).toList())
                .chartTimestamps(readings.stream().map(WeatherReadingDto::getTimestamp).toList())
                .chartPrecipitation(readings.stream().map(WeatherReadingDto::getPrecipitation).toList())
                .build();
    }
}