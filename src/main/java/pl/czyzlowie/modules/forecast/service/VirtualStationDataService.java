package pl.czyzlowie.modules.forecast.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import pl.czyzlowie.modules.forecast.client.OpenMeteoClient;
import pl.czyzlowie.modules.forecast.client.dto.OpenMeteoLightResponse;
import pl.czyzlowie.modules.forecast.entity.VirtualStation;
import pl.czyzlowie.modules.forecast.entity.VirtualStationData;
import pl.czyzlowie.modules.forecast.mapper.WeatherForecastMapper;
import pl.czyzlowie.modules.forecast.repository.VirtualStationDataRepository;
import pl.czyzlowie.modules.forecast.repository.VirtualStationRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VirtualStationDataService {

    private final VirtualStationRepository virtualStationRepository;
    private final VirtualStationDataRepository virtualStationDataRepository;
    private final OpenMeteoClient openMeteoClient;
    private final WeatherForecastMapper mapper;

    @Value("${forecast.api.url}")
    private String apiUrl;


    public void fetchAndSaveCurrentData() {
        log.info("START: Pobieranie danych bieżących (Light)...");

        List<VirtualStation> stations = virtualStationRepository.findAllByActiveTrue();
        if (stations.isEmpty()) {
            log.info("Brak aktywnych stacji wirtualnych.");
            return;
        }

        List<VirtualStationData> fetchedData = stations.stream()
                .map(station -> CompletableFuture.supplyAsync(() -> fetchDataFromApi(station)))
                .toList()
                .stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .toList();

        if (fetchedData.isEmpty()) {
            log.warn("Nie udało się pobrać danych dla żadnej stacji.");
            return;
        }

        saveNewDataOnly(fetchedData);
    }

    @Transactional
    protected void saveNewDataOnly(List<VirtualStationData> fetchedData) {
        Set<LocalDateTime> measurementTimes = fetchedData.stream()
                .map(VirtualStationData::getMeasurementTime)
                .collect(Collectors.toSet());

        Set<String> existingKeys = new HashSet<>();
        for (LocalDateTime time : measurementTimes) {
            Set<String> existingIds = virtualStationDataRepository.findStationIdsByMeasurementTime(time);
            existingIds.forEach(id -> existingKeys.add(generateKey(id, time)));
        }

        List<VirtualStationData> toSave = fetchedData.stream()
                .filter(data -> {
                    String key = generateKey(data.getVirtualStation().getId(), data.getMeasurementTime());
                    return !existingKeys.contains(key);
                })
                .toList();

        if (!toSave.isEmpty()) {
            virtualStationDataRepository.saveAll(toSave);
            log.info("SUKCES: Zapisano {} nowych pomiarów dla czasów: {}.", toSave.size(), measurementTimes);
        } else {
            log.info("SKIP: Wszystkie pobrane dane już istnieją w bazie.");
        }
    }

    private VirtualStationData fetchDataFromApi(VirtualStation station) {
        try {
            String url = buildUrl(station);
            return openMeteoClient.fetchData(url, OpenMeteoLightResponse.class)
                    .map(response -> mapper.toVirtualStationData(response, station))
                    .orElse(null);

        } catch (Exception e) {
            log.error("Błąd pobierania danych dla stacji '{}' (ID: {}): {}",
                    station.getName(), station.getId(), e.getMessage());
            return null;
        }
    }

    private String buildUrl(VirtualStation station) {
        return UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("latitude", station.getLatitude())
                .queryParam("longitude", station.getLongitude())
                .queryParam("current", "temperature_2m,apparent_temperature,rain,weather_code," +
                        "wind_speed_10m,wind_direction_10m,wind_gusts_10m," +
                        "surface_pressure,relative_humidity_2m")
                .queryParam("timezone", "Europe/Warsaw")
                .build()
                .toUriString();
    }

    private String generateKey(String stationId, LocalDateTime time) {
        return stationId + "_" + time;
    }
}