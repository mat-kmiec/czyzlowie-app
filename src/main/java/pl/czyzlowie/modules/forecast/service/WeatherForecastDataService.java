package pl.czyzlowie.modules.forecast.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import pl.czyzlowie.modules.forecast.client.OpenMeteoClient;
import pl.czyzlowie.modules.forecast.client.dto.OpenMeteoResponse;
import pl.czyzlowie.modules.forecast.entity.VirtualStation;
import pl.czyzlowie.modules.forecast.entity.WeatherForecast;
import pl.czyzlowie.modules.forecast.mapper.WeatherForecastMapper;
import pl.czyzlowie.modules.forecast.repository.VirtualStationRepository;
import pl.czyzlowie.modules.imgw.entity.ImgwSynopStation;
import pl.czyzlowie.modules.imgw.repository.ImgwSynopStationRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherForecastDataService {

    private static final int BATCH_SIZE = 10;
    private static final long RATE_LIMIT_PAUSE_MS = 1050L;
    private static final int API_TIMEOUT_SECONDS = 12;

    private static final String API_HOURLY_PARAMS = "temperature_2m,apparent_temperature,rain,weather_code," +
            "cloud_cover,wind_speed_10m,wind_direction_10m,wind_gusts_10m," +
            "surface_pressure,uv_index";
    private static final String API_DAILY_PARAMS = "sunrise,sunset,uv_index_max";

    private final ImgwSynopStationRepository synopStationRepository;
    private final VirtualStationRepository virtualStationRepository;
    private final WeatherForecastStorageService storageService;
    private final OpenMeteoClient openMeteoClient;
    private final WeatherForecastMapper mapper;
    private final Executor weatherExecutor;

    @Value("${forecast.api.url}")
    private String apiUrl;

    public void updateAllForecasts() {
        log.info("START: Aktualizacja prognoz pogody (Hourly)...");

        AtomicBoolean criticalErrorOccurred = new AtomicBoolean(false);

        List<ImgwSynopStation> synopStations = synopStationRepository.findAllByIsActiveTrue();
        processStationsBatched(synopStations,
                s -> buildUrl(s.getLatitude(), s.getLongitude()),
                mapper::toSynopForecasts,
                true,
                criticalErrorOccurred);

        if (criticalErrorOccurred.get()) {
            log.error("CRITICAL: Przerwano proces po błędach w stacjach SYNOP.");
            return;
        }

        List<VirtualStation> virtualStations = virtualStationRepository.findAllByActiveTrue();
        processStationsBatched(virtualStations,
                s -> buildUrl(s.getLatitude(), s.getLongitude()),
                mapper::toVirtualForecasts,
                false,
                criticalErrorOccurred);

        if (criticalErrorOccurred.get()) {
            log.error("CRITICAL: Przerwano proces w trakcie stacji WIRTUALNYCH.");
        } else {
            log.info("KONIEC: Aktualizacja prognoz zakończona sukcesem.");
        }
    }

    private <T> void processStationsBatched(List<T> stations,
                                            Function<T, String> urlBuilder,
                                            BiFunction<OpenMeteoResponse, T, List<WeatherForecast>> mappingStrategy,
                                            boolean isSynop,
                                            AtomicBoolean criticalErrorOccurred) {
        if (stations.isEmpty() || criticalErrorOccurred.get()) return;

        List<List<T>> batches = splitIntoBatches(stations, BATCH_SIZE);
        log.info("Plan: {} stacji podzielono na {} paczek (Typ Synop: {}).", stations.size(), batches.size(), isSynop);

        for (int i = 0; i < batches.size(); i++) {
            if (criticalErrorOccurred.get()) {
                log.warn("ABORT: Wykryto błąd krytyczny. Pomijam resztę.");
                break;
            }

            long startBatch = System.currentTimeMillis();
            List<T> batch = batches.get(i);

            List<WeatherForecast> fetchedData = fetchBatch(batch, urlBuilder, mappingStrategy, criticalErrorOccurred);

            if (!fetchedData.isEmpty() && !criticalErrorOccurred.get()) {
                try {
                    storageService.saveForecasts(fetchedData, isSynop);
                } catch (Exception e) {
                    log.error("Błąd zapisu bazy danych: {}", e.getMessage());
                }
            }

            if (i < batches.size() - 1 && !criticalErrorOccurred.get()) {
                enforceRateLimit(startBatch);
            }
        }
    }

    private <T> List<WeatherForecast> fetchBatch(List<T> batch,
                                                 Function<T, String> urlBuilder,
                                                 BiFunction<OpenMeteoResponse, T, List<WeatherForecast>> mappingStrategy,
                                                 AtomicBoolean errorFlag) {

        List<CompletableFuture<List<WeatherForecast>>> futures = batch.stream()
                .map(station -> CompletableFuture.supplyAsync(() -> {
                            if (errorFlag.get()) return Collections.<WeatherForecast>emptyList();

                            String url = urlBuilder.apply(station);
                            return openMeteoClient.fetchData(url, OpenMeteoResponse.class)
                                    .map(response -> mappingStrategy.apply(response, station))
                                    .orElse(Collections.emptyList());
                        }, weatherExecutor)
                        .orTimeout(API_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                        .exceptionally(ex -> {
                            log.error("API ERROR/TIMEOUT: {}. Ustawiam flagę CRITICAL.", ex.getMessage());
                            errorFlag.set(true);
                            return Collections.emptyList();
                        }))
                .toList();

        try {
            return futures.stream()
                    .map(CompletableFuture::join)
                    .flatMap(List::stream)
                    .toList();
        } catch (Exception e) {
            log.error("Błąd wątku/paczki: {}", e.getMessage());
            errorFlag.set(true);
            return Collections.emptyList();
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

    private String buildUrl(BigDecimal lat, BigDecimal lon) {
        return UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("latitude", lat)
                .queryParam("longitude", lon)
                .queryParam("past_days", 1)
                .queryParam("hourly", API_HOURLY_PARAMS)
                .queryParam("daily", API_DAILY_PARAMS)
                .queryParam("timezone", "Europe/Warsaw")
                .build()
                .toUriString();
    }

    private <T> List<List<T>> splitIntoBatches(List<T> list, int size) {
        List<List<T>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            batches.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return batches;
    }
}