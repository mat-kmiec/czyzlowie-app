package pl.czyzlowie.modules.forecast.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.forecast.client.OpenMeteoClient;
import pl.czyzlowie.modules.forecast.client.dto.OpenMeteoLightResponse;
import pl.czyzlowie.modules.forecast.entity.VirtualStation;
import pl.czyzlowie.modules.forecast.entity.VirtualStationData;
import pl.czyzlowie.modules.forecast.mapper.WeatherForecastMapper;
import pl.czyzlowie.modules.forecast.repository.VirtualStationDataRepository;
import pl.czyzlowie.modules.forecast.repository.VirtualStationRepository;

import java.time.LocalDateTime;
import java.util.*;
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
    private String urlTemplate;

    private static final String CURRENT_PARAMS = "&current=temperature_2m,apparent_temperature,rain,weather_code,wind_speed_10m,wind_direction_10m,wind_gusts_10m,surface_pressure,relative_humidity_2m&timezone=Europe/Warsaw";

    @Transactional
    public void fetchAndSaveCurrentData() {
        log.info("START: Pobieranie danych bieżących (Light)...");

        List<VirtualStation> stations = virtualStationRepository.findAllByActiveTrue();

        List<VirtualStationData> fetchedData = stations.parallelStream()
                .map(this::fetchDataFromApi)
                .filter(Objects::nonNull)
                .toList();

        if (fetchedData.isEmpty()) return;

        Set<LocalDateTime> times = fetchedData.stream()
                .map(VirtualStationData::getMeasurementTime)
                .collect(Collectors.toSet());

        Set<String> existingKeys = new HashSet<>();
        for (LocalDateTime time : times) {

            Set<String> existingIds = virtualStationDataRepository.findStationIdsByMeasurementTime(time);
            existingIds.forEach(id -> existingKeys.add(id + "_" + time));
        }

        List<VirtualStationData> toSave = fetchedData.stream()
                .filter(data -> {
                    String key = data.getVirtualStation().getId() + "_" + data.getMeasurementTime();
                    return !existingKeys.contains(key);
                })
                .collect(Collectors.toList());

        if (!toSave.isEmpty()) {
            virtualStationDataRepository.saveAll(toSave);
            log.info("Zapisano {} nowych pomiarów dla godziny {}.", toSave.size(), times);
        } else {
            log.info("Brak nowych danych. Wszystkie pomiary dla {} już istnieją.", times);
        }
    }

    private VirtualStationData fetchDataFromApi(VirtualStation station) {
        try {
            String fullUrl = String.format(urlTemplate, station.getLatitude(), station.getLongitude()) + CURRENT_PARAMS;
            return openMeteoClient.fetchData(fullUrl, OpenMeteoLightResponse.class)
                    .map(response -> mapper.toVirtualStationData(response, station))
                    .orElse(null);
        } catch (Exception e) {
            log.error("Błąd API dla stacji {}", station.getName());
            return null;
        }
    }
}