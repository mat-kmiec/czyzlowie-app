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
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
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
                fetchAndMap(() -> imgwSynopStationRepository.findInBounds(south, north, west, east),
                        st -> createBasicMarker(st.getId(), st.getName(), "SYNOP", st.getLatitude(), st.getLongitude())),

                fetchAndMap(() -> imgwHydroStationRepository.findInBounds(south, north, west, east),
                        h -> createBasicMarker(h.getId(), h.getName(), "HYDRO", h.getLatitude(), h.getLongitude())),

                fetchAndMap(() -> imgwMeteoStationRepository.findInBounds(south, north, west, east),
                        m -> createBasicMarker(m.getId(), m.getName(), "METEO", m.getLatitude(), m.getLongitude())),

                fetchAndMap(() -> mapSpotRepository.findInBoundsOrRestrictions(south, north, west, east),
                        this::mapSpotToDto)
        ).flatMap(Function.identity()).toList();

        log.debug("Zmapowano {} punktów dla obszaru ekranu", markers.size());
        return markers;
    }

    @Transactional(readOnly = true)
    public List<MapMarkerDto> getAllMarkers() {
        return Stream.of(
                fetchAndMap(imgwSynopStationRepository::findAll,
                        st -> createBasicMarker(st.getId(), st.getName(), "SYNOP", st.getLatitude(), st.getLongitude())),

                fetchAndMap(imgwHydroStationRepository::findAll,
                        h -> createBasicMarker(h.getId(), h.getName(), "HYDRO", h.getLatitude(), h.getLongitude())),

                fetchAndMap(imgwMeteoStationRepository::findAll,
                        m -> createBasicMarker(m.getId(), m.getName(), "METEO", m.getLatitude(), m.getLongitude())),

                fetchAndMap(mapSpotRepository::findAll,
                        this::mapSpotToDto)
        ).flatMap(Function.identity()).toList();
    }


    private <T> Stream<MapMarkerDto> fetchAndMap(Supplier<List<T>> dataProvider, Function<T, MapMarkerDto> mapper) {
        return dataProvider.get().stream()
                .map(mapper)
                .filter(Objects::nonNull);
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

        if (spot.getLatitude() == null || spot.getLongitude() == null) {
            return null;
        }

        return MapMarkerDto.builder()
                .id("SPOT_" + spot.getId())
                .name(spot.getName())
                .type(spot.getSpotType().name())
                .slug(spot.getSlug())
                .lat(BigDecimal.valueOf(spot.getLatitude()))
                .lng(BigDecimal.valueOf(spot.getLongitude()))
                .build();
    }

    private MapMarkerDto createBasicMarker(String id, String name, String type, BigDecimal lat, BigDecimal lng) {
        if (lat == null || lng == null) {
            return null;
        }

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