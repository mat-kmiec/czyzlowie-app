package pl.czyzlowie.modules.map.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.imgw.repository.ImgwHydroStationRepository;
import pl.czyzlowie.modules.imgw.repository.ImgwMeteoStationRepository;
import pl.czyzlowie.modules.imgw.repository.ImgwSynopStationRepository;
import pl.czyzlowie.modules.map.dto.MapMarkerDto;
import pl.czyzlowie.modules.map.repository.MapSpotRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapService {


    private final ImgwSynopStationRepository imgwSynopStationRepository;
    private final ImgwHydroStationRepository imgwHydroStationRepository;
    private final ImgwMeteoStationRepository imgwMeteoStationRepository;
    private final MapSpotRepository mapSpotRepository;

    public List<MapMarkerDto> getAllMarkers() {
        List<MapMarkerDto> markers = new ArrayList<>();

        // 1. SYNOP
        markers.addAll(imgwSynopStationRepository.findAll().stream()
                .filter(s -> s.getLatitude() != null && s.getLongitude() != null)
                .map(s -> createMarker(s.getId(), s.getName(), "SYNOP", s.getLatitude(), s.getLongitude()))
                .toList());

        // 2. HYDRO
        markers.addAll(imgwHydroStationRepository.findAll().stream()
                .filter(h -> h.getLatitude() != null && h.getLongitude() != null)
                .map(h -> createMarker(h.getId(), h.getName(), "HYDRO", h.getLatitude(), h.getLongitude()))
                .toList());

        // 3. METEO
        markers.addAll(imgwMeteoStationRepository.findAll().stream()
                .filter(m -> m.getLatitude() != null && m.getLongitude() != null)
                .map(m -> createMarker(m.getId(), m.getName(), "METEO", m.getLatitude(), m.getLongitude()))
                .toList());

        markers.addAll(mapSpotRepository.findAll().stream()
                .map(spot -> MapMarkerDto.builder()
                        .id("SPOT_" + spot.getId())
                        .name(spot.getName())
                        .type(spot.getSpotType().name()) // Np. "LAKE", "LAUNCH"
                        .slug(spot.getSlug())
                        .lat(BigDecimal.valueOf(spot.getLatitude()))
                        .lng(BigDecimal.valueOf(spot.getLongitude()))
                        .build())
                .toList());

        log.info("Zmapowano łącznie {} punktów IMGW", markers.size());
        return markers;
    }

    private MapMarkerDto createMarker(String id, String name, String type, BigDecimal lat, BigDecimal lng) {
        return MapMarkerDto.builder()
                .id(type + "_" + id)
                .name(name)
                .type(type)
                .slug(id)
                .lat(lat)
                .lng(lng)
                .build();
    }
}
