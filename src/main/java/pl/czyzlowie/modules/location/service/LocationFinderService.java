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

    private List<StationPoint> loadHydroStations() {
        return hydroRepository.findActiveStationCoordinates().stream()
                .filter(this::isValidCoordinate)
                .map(s -> new StationPoint(s.getId(), StationType.IMGW_HYDRO, s.getLatitude().doubleValue(), s.getLongitude().doubleValue()))
                .toList();
    }

    private List<StationPoint> loadMeteoStations() {
        return meteoRepository.findActiveStationCoordinates().stream()
                .filter(this::isValidCoordinate)
                .map(s -> new StationPoint(s.getId(), StationType.IMGW_METEO, s.getLatitude().doubleValue(), s.getLongitude().doubleValue()))
                .toList();
    }

    private boolean isValidCoordinate(StationCoordinatesView view) {
        return view.getLatitude() != null && view.getLongitude() != null;
    }

    private void validateCoordinates(double lat, double lon) {
        if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
            throw new IllegalArgumentException("Nieprawidłowe współrzędne geograficzne: " + lat + ", " + lon);
        }
    }

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