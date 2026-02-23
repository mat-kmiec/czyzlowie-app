package pl.czyzlowie.modules.barometer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.barometer.dto.BarometerViewDto;
import pl.czyzlowie.modules.barometer.entity.StationBarometerStats;
import pl.czyzlowie.modules.barometer.mapper.BarometerViewMapper;
import pl.czyzlowie.modules.barometer.repository.StationBarometerStatsRepository;
import pl.czyzlowie.modules.imgw.entity.ImgwSynopStation;
import pl.czyzlowie.modules.imgw.repository.ImgwSynopStationRepository;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BarometerViewService {

    private final ImgwSynopStationRepository synopRepository;
    private final StationBarometerStatsRepository statsRepository;
    private final BarometerViewMapper mapper;

    @Transactional(readOnly = true)
    public BarometerViewDto getBarometerDataForView(Double lat, Double lon, String locationName) {
        log.info("Szukam najbliższej stacji z danymi dla: {} ({}, {})", locationName, lat, lon);

        if (lat == null || lon == null) {
            log.info("Brak współrzędnych (null) - ustawiam domyślną lokalizację: Warszawa");
            lat = 52.2297;
            lon = 21.0122;
            if (locationName == null || locationName.isEmpty()) {
                locationName = "Warszawa";
            }
        }

        Double finalLat = lat;
        Double finalLon = lon;
        String finalLocationName = locationName;

        List<ImgwSynopStation> allStations = synopRepository.findAllByIsActiveTrue();

        return allStations.stream()
                .sorted(Comparator.comparingDouble(s -> calculateDistance(finalLat, finalLon, s.getLatitude().doubleValue(), s.getLongitude().doubleValue())))
                .filter(s -> statsRepository.existsByIdStationId(s.getId()))
                .findFirst()
                .map(station -> {
                    StationBarometerStats stats = statsRepository.findByIdStationId(station.getId())
                            .orElseThrow();

                    log.info("Dopasowano lokalizację {} do stacji z danymi: {} (ID: {})",
                            finalLocationName, station.getName(), station.getId());

                    return mapper.toDto(stats, finalLocationName);
                })
                .orElseGet(() -> {
                    log.warn("Nie znaleziono żadnej stacji z danymi barometrycznymi w promieniu działania aplikacji!");
                    return buildEmptyDto(finalLocationName);
                });
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2))
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        return dist * 60 * 1.1515 * 1.609344;
    }

    private BarometerViewDto buildEmptyDto(String name) {
        return BarometerViewDto.builder()
                .locationName(name)
                .conditionTitle("Brak danych")
                .conditionDescription("Niestety, najbliższe stacje meteorologiczne nie raportują obecnie ciśnienia dla tej lokalizacji.")
                .conditionColorClass("text-muted")
                .build();
    }
}