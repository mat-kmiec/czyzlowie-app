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
import pl.czyzlowie.modules.forecast.repository.WeatherImportLogRepository;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * The WeatherForecastDataService is responsible for managing the retrieval and processing of weather forecast data
 * for synoptic and virtual weather stations. It handles fetching external API data, transforming it into domain-specific
 * objects, and storing the results. The service ensures data is processed in batches and adheres to rate limiting and timeout
 * policies to prevent system overloads or API violations.
 */
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
    private final WeatherImportMonitor importMonitor;

    @Value("${forecast.api.url}")
    private String apiUrl;

    /**
     * Updates weather forecasts for both synoptic and virtual stations by fetching data from external sources.
     *
     * The method performs the following operations:
     * - Retrieves active synoptic stations and processes them in batches, converting retrieved data into synoptic forecasts.
     * - If no critical errors occur during the synoptic station update, retrieves active virtual stations and processes them in batches, converting the data into virtual forecasts
     * .
     * - Logs the import process, indicating the source, type, number of processed records, and whether a critical error occurred.
     * - Outputs appropriate log messages to indicate the start, success, or failure of the update process.
     *
     * Critical errors during processing of synoptic or virtual stations will halt subsequent updates and result in an appropriate error log.
     */
    public void updateAllForecasts() {
        log.info("START: Aktualizacja prognoz pogody (Hourly)...");

        AtomicBoolean criticalErrorOccurred = new AtomicBoolean(false);
        AtomicInteger totalRecords = new AtomicInteger(0);

        try {
            // 1. Synop
            List<ImgwSynopStation> synopStations = synopStationRepository.findAllByIsActiveTrue();
            processStationsBatched(synopStations,
                    s -> buildUrl(s.getLatitude(), s.getLongitude()),
                    mapper::toSynopForecasts,
                    true, criticalErrorOccurred, totalRecords);

            // 2. Virtual
            if (!criticalErrorOccurred.get()) {
                List<VirtualStation> virtualStations = virtualStationRepository.findAllByActiveTrue();
                processStationsBatched(virtualStations,
                        s -> buildUrl(s.getLatitude(), s.getLongitude()),
                        mapper::toVirtualForecasts,
                        false, criticalErrorOccurred, totalRecords);
            }
        } finally {
            importMonitor.logImport("OPEN_METEO", "FORECAST_HOURLY", totalRecords.get(), criticalErrorOccurred.get());
        }

        if (criticalErrorOccurred.get()) {
            log.error("KONIEC: Proces aktualizacji zakończony błędem krytycznym.");
        } else {
            log.info("KONIEC: Aktualizacja prognoz zakończona sukcesem.");
        }
    }

    /**
     * Processes a list of station data in batches, fetches weather forecast data,
     * and saves the data to storage. It supports custom strategies for building URLs
     * and mapping the response to weather forecast objects. The process stops if a critical error occurs.
     *
     * @param stations the list of station objects to be processed
     * @param urlBuilder a function to build the URL for each station
     *                   based on the station object
     * @param mappingStrategy a strategy to map the response to a list of weather forecast objects
     * @param isSynop a flag indicating the type of station (true if Synop, false otherwise)
     * @param criticalErrorOccurred an atomic boolean flag indicating if a critical error has occurred
     *                              that should abort the process
     * @param recordCounter an atomic counter used*/
    private <T> void processStationsBatched(List<T> stations,
                                            Function<T, String> urlBuilder,
                                            BiFunction<OpenMeteoResponse, T, List<WeatherForecast>> mappingStrategy,
                                            boolean isSynop,
                                            AtomicBoolean criticalErrorOccurred,
                                            AtomicInteger recordCounter) {
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
                    recordCounter.addAndGet(fetchedData.size());
                } catch (Exception e) {
                    log.error("Błąd zapisu bazy danych: {}", e.getMessage());
                }
            }

            if (i < batches.size() - 1 && !criticalErrorOccurred.get()) {
                enforceRateLimit(startBatch);
            }
        }
    }

    /**
     * Fetches a batch of weather forecasts by asynchronously processing a list of items and mapping
     * the responses from an API.
     *
     * @param batch The list of input items, each representing a unit of data to process.
     * @param urlBuilder A function to build the URL for each item in the batch.
     * @param mappingStrategy A bi-function used to map the API response to a list of weather forecasts
     *                        based on the input item.
     * @param errorFlag An atomic boolean flag to track if an error has occurred and interrupt processing
     *                  if necessary.
     * @return A list of weather forecasts derived from successfully processed items in the batch.
     *         Returns an empty list if an error occurs or no forecasts are available.
     */
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

    /**
     * Ensures that the execution respects a rate limit by enforcing a pause
     * between consecutive operations if the elapsed time since the start of
     * the batch is less than the predefined rate limit pause duration.
     *
     * @param startBatchTime the start time of the batch process in milliseconds
     *                       since the epoch, used to calculate the elapsed time
     *                       and enforce the rate limit.
     */
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

    /**
     * Constructs a URL string with the specified latitude and longitude, applying predefined query parameters.
     *
     * @param lat the latitude value to be included as a query parameter
     * @param lon the longitude value to be included as a query parameter
     * @return the constructed URL as a string
     */
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

    /**
     * Splits a given list into smaller batches of a specified size.
     *
     * @param list the list to be split into batches
     * @param size the size of each batch; must be a positive integer
     * @return a list of batches, where each batch is a sublist of the original list
     *         and the last batch may contain fewer elements if the total count is not divisible by the batch size
     */
    private <T> List<List<T>> splitIntoBatches(List<T> list, int size) {
        List<List<T>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            batches.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return batches;
    }
}