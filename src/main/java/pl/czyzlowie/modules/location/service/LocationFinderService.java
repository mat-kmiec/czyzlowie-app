package pl.czyzlowie.modules.location.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.barometer.entity.StationType;
import pl.czyzlowie.modules.forecast.repository.VirtualStationRepository;
import pl.czyzlowie.modules.imgw_api.repository.ImgwHydroStationRepository;
import pl.czyzlowie.modules.imgw_api.repository.ImgwMeteoStationRepository;
import pl.czyzlowie.modules.imgw_api.repository.ImgwSynopStationRepository;
import pl.czyzlowie.modules.location.enums.StationCategory;
import pl.czyzlowie.modules.moon.projection.StationCoordinatesView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * LocationFinderService is responsible for locating the nearest meteorological, hydrological,
 * or synoptic stations based on a given geographical location. The service maintains
 * an in-memory cache of station data to facilitate efficient querying.
 *
 * This service loads and stores active station coordinates for various station categories,
 * and provides functionality to find the nearest station using the Haversine formula
 * to calculate distances. The cache is initialized at startup.
 *
 * Primary features:
 * 1. Caches station data for synoptic, hydrological, and meteorological stations,
 *    including active virtual stations.
 * 2. Calculates the nearest station based on the target latitude, longitude, and station category.
 * 3. Validates geographic coordinates to ensure accuracy.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LocationFinderService {

    private final ImgwSynopStationRepository synopRepository;
    private final ImgwHydroStationRepository hydroRepository;
    private final ImgwMeteoStationRepository meteoRepository;
    private final VirtualStationRepository virtualRepository;
    private final Map<StationCategory, List<StationPoint>> stationsCache = new ConcurrentHashMap<>();

    public record NearestStation(String stationId, StationType type, double distanceKm) {}
    private record StationPoint(String id, StationType type, double lat, double lon) {}

    /**
     * Initializes the in-memory cache of station coordinates upon application startup.
     *
     * This method loads station data from configured repositories for various station categories,
     * including synoptic stations, hydro stations, and meteorological stations. The data is
     * organized and stored in a thread-safe map (`stationsCache`) for efficient access during
     * location-based searches.
     *
     * Logging is used to provide detailed information about the loading process and to confirm the
     * number of stations loaded for each category.
     *
     * The categories of stations loaded are:
     * - SYNOPTIC: Includes synoptic and virtual station data.
     * - HYDRO: Includes hydrological station data.
     * - METEO: Includes meteorological station data.
     *
     * The cache is critical for optimizing key functionalities of the service, such as finding
     * the nearest station for a given location and station category.
     *
     * This method is executed automatically after the construction of the service bean.
     */
    @PostConstruct
    public void initStationCache() {
        log.info("Rozpoczynam ładowanie współrzędnych stacji do pamięci podręcznej...");

        stationsCache.put(StationCategory.SYNOPTIC, loadSynopticStations());
        stationsCache.put(StationCategory.HYDRO, loadHydroStations());
        stationsCache.put(StationCategory.METEO, loadMeteoStations());

        log.info("Pomyślnie załadowano stacje. Synop/Virtual: {}, Hydro: {}, Meteo: {}",
                stationsCache.get(StationCategory.SYNOPTIC).size(),
                stationsCache.get(StationCategory.HYDRO).size(),
                stationsCache.get(StationCategory.METEO).size());
    }

    /**
     * Finds the nearest station of the specified category to the given latitude and longitude.
     *
     * @param targetLat the latitude of the target location
     * @param targetLon the longitude of the target location
     * @param category the category of the station to search for
     * @return the nearest station details including station ID, type, and distance
     * @throws IllegalStateException if there are no active stations in the cache for the specified category
     */
    public NearestStation findNearestStation(double targetLat, double targetLon, StationCategory category) {
        validateCoordinates(targetLat, targetLon);

        List<StationPoint> stationsToSearch = stationsCache.getOrDefault(category, Collections.emptyList());

        if (stationsToSearch.isEmpty()) {
            throw new IllegalStateException("Brak aktywnych stacji w pamięci dla kategorii: " + category);
        }

        return stationsToSearch.stream()
                .min(Comparator.comparingDouble(s -> calculateHaversineDistance(targetLat, targetLon, s.lat(), s.lon())))
                .map(nearest -> {
                    double distance = calculateHaversineDistance(targetLat, targetLon, nearest.lat(), nearest.lon());
                    log.debug("Dla punktu [{}, {}] w kategorii {} najbliższa stacja to {} ({} km)",
                            targetLat, targetLon, category, nearest.id(), String.format("%.1f", distance));
                    return new NearestStation(nearest.id(), nearest.type(), distance);
                })
                .orElseThrow(() -> new IllegalStateException("Nie udało się dopasować żadnej stacji."));
    }

    /**
     * Loads a list of synoptic stations by retrieving and processing active station coordinates
     * from both the synop repository and the virtual repository. Filters out invalid coordinates
     * and creates {@code StationPoint} objects for both IMGW_SYNOP and VIRTUAL station types.
     *
     * @return a list of {@code StationPoint} objects representing valid synoptic stations
     */
    private List<StationPoint> loadSynopticStations() {
        List<StationPoint> points = new ArrayList<>();

        synopRepository.findActiveStationCoordinates().stream()
                .filter(this::isValidCoordinate)
                .map(s -> new StationPoint(s.getId(), StationType.IMGW_SYNOP, s.getLatitude().doubleValue(), s.getLongitude().doubleValue()))
                .forEach(points::add);

        virtualRepository.findActiveStationCoordinates().stream()
                .filter(this::isValidCoordinate)
                .map(v -> new StationPoint(v.getId(), StationType.VIRTUAL, v.getLatitude().doubleValue(), v.getLongitude().doubleValue()))
                .forEach(points::add);

        return points;
    }

    /**
     * Loads and processes active hydro station coordinates from the repository.
     * Filters the coordinates to ensure validity and maps them to StationPoint objects.
     *
     * @return a list of StationPoint objects representing valid hydro station coordinates
     */
    private List<StationPoint> loadHydroStations() {
        return hydroRepository.findActiveStationCoordinates().stream()
                .filter(this::isValidCoordinate)
                .map(s -> new StationPoint(s.getId(), StationType.IMGW_HYDRO, s.getLatitude().doubleValue(), s.getLongitude().doubleValue()))
                .toList();
    }

    /**
     * Loads the list of active meteorological stations by retrieving their coordinates
     * from the repository, filtering invalid coordinates, and mapping the data
     * to a list of {@code StationPoint} objects.
     *
     * @return a list of {@code StationPoint} objects representing active meteorological stations
     * with valid coordinates
     */
    private List<StationPoint> loadMeteoStations() {
        return meteoRepository.findActiveStationCoordinates().stream()
                .filter(this::isValidCoordinate)
                .map(s -> new StationPoint(s.getId(), StationType.IMGW_METEO, s.getLatitude().doubleValue(), s.getLongitude().doubleValue()))
                .toList();
    }

    /**
     * Checks if the given StationCoordinatesView has valid latitude and longitude values.
     *
     * @param view the StationCoordinatesView object to validate
     * @return true if both latitude and longitude are not null, otherwise false
     */
    private boolean isValidCoordinate(StationCoordinatesView view) {
        return view.getLatitude() != null && view.getLongitude() != null;
    }

    /**
     * Validates the geographic coordinates to ensure they are within valid ranges.
     *
     * @param lat the latitude value, must be between -90 and 90 inclusive
     * @param lon the longitude value, must be between -180 and 180 inclusive
     * @throws IllegalArgumentException if the latitude or longitude values are out of range
     */
    private void validateCoordinates(double lat, double lon) {
        if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
            throw new IllegalArgumentException("Nieprawidłowe współrzędne geograficzne: " + lat + ", " + lon);
        }
    }

    /**
     * Calculates the Haversine distance between two geographic points specified by their
     * latitude and longitude coordinates. The distance is measured in kilometers.
     *
     * @param lat1 the latitude of the first point in decimal degrees
     * @param lon1 the longitude of the first point in decimal degrees
     * @param lat2 the latitude of the second point in decimal degrees
     * @param lon2 the longitude of the second point in decimal degrees
     * @return the Haversine distance between the two points in kilometers
     */
    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}