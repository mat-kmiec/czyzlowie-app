package pl.czyzlowie.modules.location.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.barometer.entity.StationType;
import pl.czyzlowie.modules.forecast.repository.VirtualStationRepository;
import pl.czyzlowie.modules.imgw_api.repository.ImgwHydroStationRepository;
import pl.czyzlowie.modules.imgw_api.repository.ImgwMeteoStationRepository;
import pl.czyzlowie.modules.imgw_api.repository.ImgwSynopStationRepository;
import pl.czyzlowie.modules.location.enums.StationCategory;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationFinderService {

    private final ImgwSynopStationRepository synopRepository;
    private final VirtualStationRepository virtualRepository;

     private final ImgwHydroStationRepository hydroRepository;
     private final ImgwMeteoStationRepository meteoRepository;

    public record NearestStation(String stationId, StationType type, double distanceKm) {}
    private record StationPoint(String id, StationType type, double lat, double lon) {}

    public NearestStation findNearestStation(double targetLat, double targetLon, StationCategory category) {
        List<StationPoint> stationsToSearch = getStationsByCategory(category);

        if (stationsToSearch.isEmpty()) {
            throw new RuntimeException("Brak aktywnych stacji w bazie dla kategorii: " + category);
        }

        StationPoint nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (StationPoint station : stationsToSearch) {
            double distance = calculateHaversineDistance(targetLat, targetLon, station.lat(), station.lon());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = station;
            }
        }

        log.info("Dla punktu [{}, {}] w kategorii {} najbliższa stacja to {} ({} km)",
                targetLat, targetLon, category, nearest.id(), String.format("%.1f", minDistance));

        return new NearestStation(nearest.id(), nearest.type(), minDistance);
    }

    private List<StationPoint> getStationsByCategory(StationCategory category) {
        List<StationPoint> points = new ArrayList<>();

        switch (category) {
            case SYNOPTIC -> {
                synopRepository.findAllByIsActiveTrue().forEach(s -> {
                    if (s.getLatitude() != null && s.getLongitude() != null)
                        points.add(new StationPoint(s.getId(), StationType.IMGW_SYNOP, s.getLatitude().doubleValue(), s.getLongitude().doubleValue()));
                });
                virtualRepository.findAllByActiveTrue().forEach(v -> {
                    if (v.getLatitude() != null && v.getLongitude() != null)
                        points.add(new StationPoint(v.getId(), StationType.VIRTUAL, v.getLatitude().doubleValue(), v.getLongitude().doubleValue()));
                });
            }
            case HYDRO -> {
                // TODO: HYDRO
                /*
                hydroRepository.findAllByIsActiveTrue().forEach(h -> {
                    if (h.getLatitude() != null && h.getLongitude() != null)
                        points.add(new StationPoint(h.getId(), StationType.IMGW_HYDRO, h.getLatitude(), h.getLongitude()));
                });
                */
                log.warn("Kategoria HYDRO nie jest jeszcze w pełni zaimplementowana.");
            }
            case METEO -> {
                // TODO: METEO
                /*
                meteoRepository.findAllByIsActiveTrue().forEach(m -> {
                    if (m.getLatitude() != null && m.getLongitude() != null)
                        points.add(new StationPoint(m.getId(), StationType.IMGW_METEO, m.getLatitude(), m.getLongitude()));
                });
                */
                log.warn("Kategoria METEO nie jest jeszcze w pełni zaimplementowana.");
            }
        }

        return points;
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}