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
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VirtualStationDataService {

    private static final int BATCH_SIZE = 7;
    private static final long RATE_LIMIT_PAUSE_MS = 1100L;

    private static final String API_PARAMS = "temperature_2m,apparent_temperature,rain,weather_code," +
            "wind_speed_10m,wind_direction_10m,wind_gusts_10m," +
            "surface_pressure,relative_humidity_2m";
    private static final String TIMEZONE = "Europe/Warsaw";

    private final VirtualStationRepository virtualStationRepository;
    private final VirtualStationDataRepository virtualStationDataRepository;
    private final OpenMeteoClient openMeteoClient;
    private final WeatherForecastMapper mapper;
    private final ExecutorService taskExecutor = Executors.newFixedThreadPool(4);

    @Value("${forecast.api.url}")
    private String apiUrl;

    public void fetchAndSaveCurrentData() {
        log.info("START: Pobieranie danych bieżących (Light)...");

        List<VirtualStation> stations = virtualStationRepository.findAllByActiveTrue();
        if (stations.isEmpty()) {
            log.info("Brak aktywnych stacji wirtualnych.");
            return;
        }

        AtomicBoolean criticalErrorOccurred = new AtomicBoolean(false);

        List<List<VirtualStation>> batches = splitIntoBatches(stations, BATCH_SIZE);
        log.info("Plan: {} stacji podzielono na {} paczek.", stations.size(), batches.size());

        for (int i = 0; i < batches.size(); i++) {
            if (criticalErrorOccurred.get()) {
                log.warn("ABORT: Wykryto błąd krytyczny. Przerywam pobieranie pozostałych paczek.");
                break;
            }

            long startBatch = System.currentTimeMillis();
            List<VirtualStation> batch = batches.get(i);
            List<VirtualStationData> fetchedData = fetchBatch(batch, criticalErrorOccurred);

            if (!fetchedData.isEmpty() && !criticalErrorOccurred.get()) {
                saveNewDataOnly(fetchedData);
            }

            if (i < batches.size() - 1 && !criticalErrorOccurred.get()) {
                enforceRateLimit(startBatch);
            }
        }

        if (!criticalErrorOccurred.get()) {
            log.info("KONIEC: Pobieranie danych bieżących zakończone sukcesem.");
        }
    }

    private List<VirtualStationData> fetchBatch(List<VirtualStation> batch, AtomicBoolean errorFlag) {
        List<CompletableFuture<VirtualStationData>> futures = batch.stream()
                .map(station -> CompletableFuture.supplyAsync(() -> {
                    if (errorFlag.get()) return null;

                    try {
                        String url = buildUrl(station);
                        // Mapper.toVirtualStationData tworzy nowy obiekt (nie potrzebujemy update'u)
                        return openMeteoClient.fetchData(url, OpenMeteoLightResponse.class)
                                .map(response -> mapper.toVirtualStationData(response, station))
                                .orElse(null);
                    } catch (Exception e) {
                        log.error("API ERROR dla stacji '{}': {}. Ustawiam flagę CRITICAL.", station.getName(), e.getMessage());
                        errorFlag.set(true);
                        throw new CompletionException(e);
                    }
                }, taskExecutor))
                .toList();

        try {
            return futures.stream()
                    .map(CompletableFuture::join)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (Exception e) {
            log.error("Błąd podczas przetwarzania paczki: {}", e.getMessage());
            errorFlag.set(true);
            return Collections.emptyList();
        }
    }

    @Transactional
    protected void saveNewDataOnly(List<VirtualStationData> fetchedData) {
        if (fetchedData.isEmpty()) return;

        Set<String> stationIds = fetchedData.stream()
                .map(d -> d.getVirtualStation().getId())
                .collect(Collectors.toSet());

        Set<LocalDateTime> times = fetchedData.stream()
                .map(VirtualStationData::getMeasurementTime)
                .collect(Collectors.toSet());

        List<VirtualStationData> existingData = virtualStationDataRepository
                .findAllByVirtualStationIdInAndMeasurementTimeIn(stationIds, times);

        Set<String> existingKeys = existingData.stream()
                .map(this::generateUniqueKey)
                .collect(Collectors.toSet());

        List<VirtualStationData> toSave = fetchedData.stream()
                .filter(data -> !existingKeys.contains(generateUniqueKey(data)))
                .toList();

        if (!toSave.isEmpty()) {
            virtualStationDataRepository.saveAll(toSave);
            log.info("Zapisano {} nowych pomiarów.", toSave.size());
        } else {
            // log.debug("Brak nowych danych do zapisu.");
        }
    }

    private void enforceRateLimit(long startBatchTime) {
        long elapsed = System.currentTimeMillis() - startBatchTime;
        long sleepTime = RATE_LIMIT_PAUSE_MS - elapsed;

        if (sleepTime > 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private String buildUrl(VirtualStation station) {
        return UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("latitude", station.getLatitude())
                .queryParam("longitude", station.getLongitude())
                .queryParam("current", API_PARAMS)
                .queryParam("timezone", TIMEZONE)
                .build()
                .toUriString();
    }

    private String generateUniqueKey(VirtualStationData data) {
        return data.getVirtualStation().getId() + "|" + data.getMeasurementTime();
    }

    private <T> List<List<T>> splitIntoBatches(List<T> list, int size) {
        List<List<T>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            batches.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return batches;
    }
}