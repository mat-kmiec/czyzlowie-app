package pl.czyzlowie.modules.map.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.imgw.repository.ImgwHydroStationRepository;
import pl.czyzlowie.modules.imgw.repository.ImgwMeteoStationRepository;
import pl.czyzlowie.modules.imgw.repository.ImgwSynopStationRepository;
import pl.czyzlowie.modules.map.dto.MapMarkerDto;
import pl.czyzlowie.modules.map.entity.MapSpot;
import pl.czyzlowie.modules.map.entity.RestrictionSpot;
import pl.czyzlowie.modules.map.repository.MapSpotRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapMarkerService {

    private final ImgwSynopStationRepository imgwSynopStationRepository;
    private final ImgwHydroStationRepository imgwHydroStationRepository;
    private final ImgwMeteoStationRepository imgwMeteoStationRepository;
    private final MapSpotRepository mapSpotRepository;

    @Transactional(readOnly = true)
    public List<MapMarkerDto> getMarkersInBounds(Double north, Double south, Double east, Double west) {
        List<MapMarkerDto> markers = Stream.of(
                getSynopMarkersInBounds(north, south, east, west),
                getHydroMarkersInBounds(north, south, east, west),
                getMeteoMarkersInBounds(north, south, east, west),
                getSpotMarkersInBounds(north, south, east, west)
        ).flatMap(List::stream).toList();

        log.debug("Zmapowano {} punktów dla obszaru ekranu", markers.size());
        return markers;
    }

    @Transactional(readOnly = true)
    public List<MapMarkerDto> getAllMarkers() {
        return Stream.of(
                getSynopMarkers(),
                getHydroMarkers(),
                getMeteoMarkers(),
                getSpotMarkers()
        ).flatMap(List::stream).toList();
    }


    private List<MapMarkerDto> getSynopMarkersInBounds(Double n, Double s, Double e, Double w) {
        return imgwSynopStationRepository.findInBounds(s, n, w, e).stream()
                .map(st -> createBasicMarker(st.getId(), st.getName(), "SYNOP", st.getLatitude(), st.getLongitude()))
                .toList();
    }

    private List<MapMarkerDto> getHydroMarkersInBounds(Double n, Double s, Double e, Double w) {
        return imgwHydroStationRepository.findInBounds(s, n, w, e).stream()
                .map(h -> createBasicMarker(h.getId(), h.getName(), "HYDRO", h.getLatitude(), h.getLongitude()))
                .toList();
    }

    private List<MapMarkerDto> getMeteoMarkersInBounds(Double n, Double s, Double e, Double w) {
        return imgwMeteoStationRepository.findInBounds(s, n, w, e).stream()
                .map(m -> createBasicMarker(m.getId(), m.getName(), "METEO", m.getLatitude(), m.getLongitude()))
                .toList();
    }

    private List<MapMarkerDto> getSpotMarkersInBounds(Double n, Double s, Double e, Double w) {
        return mapSpotRepository.findInBoundsOrRestrictions(s, n, w, e).stream()
                .map(this::mapSpotToDto)
                .toList();
    }

    private List<MapMarkerDto> getSynopMarkers() {
        return imgwSynopStationRepository.findAll().stream()
                .filter(st -> st.getLatitude() != null && st.getLongitude() != null)
                .map(st -> createBasicMarker(st.getId(), st.getName(), "SYNOP", st.getLatitude(), st.getLongitude()))
                .toList();
    }

    private List<MapMarkerDto> getHydroMarkers() {
        return imgwHydroStationRepository.findAll().stream()
                .filter(h -> h.getLatitude() != null && h.getLongitude() != null)
                .map(h -> createBasicMarker(h.getId(), h.getName(), "HYDRO", h.getLatitude(), h.getLongitude()))
                .toList();
    }

    private List<MapMarkerDto> getMeteoMarkers() {
        return imgwMeteoStationRepository.findAll().stream()
                .filter(m -> m.getLatitude() != null && m.getLongitude() != null)
                .map(m -> createBasicMarker(m.getId(), m.getName(), "METEO", m.getLatitude(), m.getLongitude()))
                .toList();
    }

    private List<MapMarkerDto> getSpotMarkers() {
        return mapSpotRepository.findAll().stream()
                .map(this::mapSpotToDto)
                .toList();
    }

    private MapMarkerDto mapSpotToDto(MapSpot spot) {
        if (spot instanceof RestrictionSpot restriction) {
            return MapMarkerDto.builder()
                    .id("SPOT_" + spot.getId())
                    .name(spot.getName())
                    .type(spot.getSpotType().name())
                    .slug(spot.getSlug())
                    .description(spot.getDescription())
                    .startDate(restriction.getStartDate())
                    .endDate(restriction.getEndDate())
                    .restrictionType(restriction.getRestrictionType() != null
                            ? restriction.getRestrictionType().name() : "TOTAL_BAN")
                    .polygonCoordinates(restriction.getPolygonCoordinates())
                    .build();
        }

        return MapMarkerDto.builder()
                .id("SPOT_" + spot.getId())
                .name(spot.getName())
                .type(spot.getSpotType().name())
                .slug(spot.getSlug())
                .lat(spot.getLatitude() != null ? BigDecimal.valueOf(spot.getLatitude()) : null)
                .lng(spot.getLongitude() != null ? BigDecimal.valueOf(spot.getLongitude()) : null)
                .build();
    }

    private MapMarkerDto createBasicMarker(String id, String name, String type, BigDecimal lat, BigDecimal lng) {
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