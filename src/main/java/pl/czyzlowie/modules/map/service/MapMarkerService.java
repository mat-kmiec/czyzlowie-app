package pl.czyzlowie.modules.map.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.imgw.repository.ImgwHydroStationRepository;
import pl.czyzlowie.modules.imgw.repository.ImgwMeteoStationRepository;
import pl.czyzlowie.modules.imgw.repository.ImgwSynopStationRepository;
import pl.czyzlowie.modules.map.dto.MapMarkerDto;
import pl.czyzlowie.modules.map.entity.MapSpot;
import pl.czyzlowie.modules.map.entity.RestrictionSpot;
import pl.czyzlowie.modules.map.repository.MapSpotRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
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

    public List<MapMarkerDto> getMarkersInBounds(Double north, Double south, Double east, Double west) {
        var synopFuture = CompletableFuture.supplyAsync(() -> fetchAndMap(
                () -> imgwSynopStationRepository.findInBounds(south, north, west, east),
                st -> createBasicMarker(st.getId(), st.getName(), "SYNOP", toDouble(st.getLatitude()), toDouble(st.getLongitude()))));

        var hydroFuture = CompletableFuture.supplyAsync(() -> fetchAndMap(
                () -> imgwHydroStationRepository.findInBounds(south, north, west, east),
                h -> createBasicMarker(h.getId(), h.getName(), "HYDRO", toDouble(h.getLatitude()), toDouble(h.getLongitude()))));

        var meteoFuture = CompletableFuture.supplyAsync(() -> fetchAndMap(
                () -> imgwMeteoStationRepository.findInBounds(south, north, west, east),
                m -> createBasicMarker(m.getId(), m.getName(), "METEO", toDouble(m.getLatitude()), toDouble(m.getLongitude()))));

        var spotFuture = CompletableFuture.supplyAsync(() -> fetchAndMap(
                () -> mapSpotRepository.findInBounds(south, north, west, east),
                this::mapSpotToDto));

        List<MapMarkerDto> markers = Stream.of(synopFuture, hydroFuture, meteoFuture, spotFuture)
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .toList();

        log.debug("Zmapowano {} punktów dla obszaru ekranu", markers.size());
        return markers;
    }

    public List<MapMarkerDto> getAllMarkers() {
        var synopFuture = CompletableFuture.supplyAsync(() -> fetchAndMap(
                imgwSynopStationRepository::findAll,
                st -> createBasicMarker(st.getId(), st.getName(), "SYNOP", toDouble(st.getLatitude()), toDouble(st.getLongitude()))));

        var hydroFuture = CompletableFuture.supplyAsync(() -> fetchAndMap(
                imgwHydroStationRepository::findAll,
                h -> createBasicMarker(h.getId(), h.getName(), "HYDRO", toDouble(h.getLatitude()), toDouble(h.getLongitude()))));

        var meteoFuture = CompletableFuture.supplyAsync(() -> fetchAndMap(
                imgwMeteoStationRepository::findAll,
                m -> createBasicMarker(m.getId(), m.getName(), "METEO", toDouble(m.getLatitude()), toDouble(m.getLongitude()))));

        var spotFuture = CompletableFuture.supplyAsync(() -> fetchAndMap(
                mapSpotRepository::findAll,
                this::mapSpotToDto));

        return Stream.of(synopFuture, hydroFuture, meteoFuture, spotFuture)
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .toList();
    }

    private <T> List<MapMarkerDto> fetchAndMap(Supplier<List<T>> dataProvider, Function<T, MapMarkerDto> mapper) {
        return dataProvider.get().stream()
                .map(mapper)
                .filter(Objects::nonNull)
                .toList();
    }

    private MapMarkerDto mapSpotToDto(MapSpot spot) {
        if (spot instanceof RestrictionSpot restriction) {
            LocalDate today = LocalDate.now();

            if (restriction.getStartDate() != null && today.isBefore(restriction.getStartDate())) {
                return null;
            }
            if (restriction.getEndDate() != null && today.isAfter(restriction.getEndDate())) {
                return null;
            }

            return MapMarkerDto.builder()
                    .id("SPOT_" + restriction.getId())
                    .name(restriction.getName())
                    .type(restriction.getSpotType().name())
                    .slug(restriction.getSlug())
                    .description(restriction.getDescription())
                    .startDate(restriction.getStartDate())
                    .endDate(restriction.getEndDate())
                    .restrictionType(restriction.getRestrictionType() != null
                            ? restriction.getRestrictionType().name() : "TOTAL_BAN")
                    .polygonCoordinates(restriction.getPolygonCoordinates())
                    .lat(restriction.getLatitude())
                    .lng(restriction.getLongitude())
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
                .lat(spot.getLatitude())
                .lng(spot.getLongitude())
                .build();
    }

    private MapMarkerDto createBasicMarker(String id, String name, String type, Double lat, Double lng) {
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

    private Double toDouble(BigDecimal value) {
        return value == null ? null : value.doubleValue();
    }
}