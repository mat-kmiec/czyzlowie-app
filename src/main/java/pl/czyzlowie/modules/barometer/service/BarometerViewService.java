package pl.czyzlowie.modules.barometer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.barometer.dto.BarometerViewDto;
import pl.czyzlowie.modules.barometer.mapper.BarometerViewMapper;
import pl.czyzlowie.modules.barometer.repository.StationBarometerStatsRepository;
import pl.czyzlowie.modules.imgw_api.repository.ImgwSynopStationRepository;

import java.util.Comparator;

/**
 * Service class used for managing barometer data and preparing the information required for barometer views.
 * This service integrates data from meteorological stations, calculates distances to determine the nearest station,
 * and maps the relevant data into a DTO for presentation purposes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BarometerViewService {

    private static final double DEFAULT_LAT = 52.2297;
    private static final double DEFAULT_LON = 21.0122;
    private static final String DEFAULT_LOCATION_NAME = "Warszawa";
    private final ImgwSynopStationRepository synopRepository;
    private final StationBarometerStatsRepository statsRepository;
    private final BarometerViewMapper mapper;

    /**
     * Retrieves barometer data for a specific geographical location or fallback location
     * if latitude, longitude, or location name are not provided. The method attempts to
     * find the nearest active station with barometer data, calculates distances, maps the
     * data, and returns a corresponding DTO.
     *
     * @param lat          the latitude of the target location; if null, a default latitude is used
     * @param lon          the longitude of the target location; if null, a default longitude is used
     * @param locationName the name of the target location; if null or blank, a default name is used
     * @return a {@code BarometerViewDto} containing barometer data for the given or fallback location;
     *         if no station contains data for the given/fallback location, a DTO with empty values is returned
     */
    @Transactional(readOnly = true)
    public BarometerViewDto getBarometerDataForView(Double lat, Double lon, String locationName) {
        log.info("Szukam najbliższej stacji z danymi dla: {} ({}, {})", locationName, lat, lon);

        var targetLat = (lat != null) ? lat : DEFAULT_LAT;
        var targetLon = (lon != null) ? lon : DEFAULT_LON;
        var targetName = (locationName != null && !locationName.isBlank()) ? locationName : DEFAULT_LOCATION_NAME;

        if (lat == null || lon == null) {
            log.info("Brak współrzędnych - ustawiam domyślną lokalizację: {}", targetName);
        }

        return synopRepository.findAllByIsActiveTrue()
                .stream()
                .sorted(Comparator.comparingDouble(s -> calculateDistance(
                        targetLat, targetLon,
                        s.getLatitude().doubleValue(), s.getLongitude().doubleValue())))
                .flatMap(station -> statsRepository.findByIdStationId(station.getId()).stream())
                .findFirst()
                .map(stats -> {
                    log.info("Dopasowano lokalizację {} do stacji z danymi.", targetName);
                    return mapper.toDto(stats, targetName);
                })
                .orElseGet(() -> {
                    log.warn("Nie znaleziono żadnej stacji z danymi barometrycznymi!");
                    return buildEmptyDto(targetName);
                });
    }

    /**
     * Calculates the distance between two geographical points specified by their
     * latitude and longitude coordinates.
     *
     * @param lat1 the latitude of the first point
     * @param lon1 the longitude of the first point
     * @param lat2 the latitude of the second point
     * @param lon2 the longitude of the second point
     * @return the distance between the two points in kilometers
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2))
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        return dist * 60 * 1.1515 * 1.609344;
    }

    /**
     * Builds and returns an empty BarometerViewDto with predefined values
     * for missing barometer data, using the specified location name.
     *
     * @param name the name of the location to be set in the DTO
     * @return a BarometerViewDto initialized with default values indicating no data is available
     */
    private BarometerViewDto buildEmptyDto(String name) {
        return BarometerViewDto.builder()
                .locationName(name)
                .conditionTitle("Brak danych")
                .conditionDescription("Niestety, najbliższe stacje meteorologiczne nie raportują obecnie ciśnienia dla tej lokalizacji.")
                .conditionColorClass("text-muted")
                .build();
    }
}