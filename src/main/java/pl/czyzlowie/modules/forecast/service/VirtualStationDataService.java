package pl.czyzlowie.modules.forecast.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import pl.czyzlowie.modules.forecast.client.OpenMeteoClient;
import pl.czyzlowie.modules.forecast.client.dto.OpenMeteoLightResponse;
import pl.czyzlowie.modules.forecast.entity.VirtualStation;
import pl.czyzlowie.modules.forecast.entity.VirtualStationData;
import pl.czyzlowie.modules.forecast.mapper.WeatherForecastMapper;
import pl.czyzlowie.modules.forecast.repository.VirtualStationRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Service responsible for managing and processing virtual station data. It interacts with
 * the repository, external API client, data storage service, and mappers to fetch, process,
 * and store weather data. The service is designed to handle concurrent processing, rate limits,
 * and error resilience to ensure robustness in data handling.
 *
 * Key functionalities include:
 * - Fetching current weather data for active virtual stations.
 * - Processing data in batch mode to handle load scalability.
 * - Interacting with OpenMeteo API for data retrieval.
 * - Storing the processed data efficiently in a database.
 * - Supporting asynchronous and timeout-controlled operations for optimal execution.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VirtualStationDataService {

    private static final int BATCH_SIZE = 7;
    private static final long RATE_LIMIT_PAUSE_MS = 1100L;
    private static final int API_TIMEOUT_SECONDS = 8;

    private static final String API_PARAMS = "temperature_2m,apparent_temperature,rain,weather_code," +
            "wind_speed_10m,wind_direction_10m,wind_gusts_10m," +
            "surface_pressure,relative_humidity_2m";
    private static final String TIMEZONE = "Europe/Warsaw";

    private final VirtualStationRepository virtualStationRepository;
    private final VirtualStationStorageService storageService;
    private final OpenMeteoClient openMeteoClient;
    private final WeatherForecastMapper mapper;
    private final WeatherImportMonitor importMonitor;

    private final Executor weatherExecutor;

    @Value("${forecast.api.url}")
    private String apiUrl;

    /**
     * Fetches the current data for all active virtual stations and saves the fetched data into storage.
     * The operation is performed in batches to handle large numbers of virtual stations efficiently.
     * Additionally, it incorporates error handling and rate limiting to ensure robust execution.
     *
     * The method performs the following steps:
     * - Retrieves all active virtual stations from the repository.
     * - Splits the stations into smaller batches to process them sequentially.
     * - For each batch, attempts to fetch associated data from an external source.
     * - If data is successfully fetched, it is saved into the database.
     * - Logs the results of the operation and any errors encountered during the process.
     * - Stops processing if a critical error is detected in any batch.
     *
     * Logging and monitoring:
     * - Logs the start and completion of the data retrieval process.
     * - Logs critical errors and aborts further processing if such errors occur.
     * - Tracks the number of successfully saved records and logs the import status.
     *
     * Rate limiting:
     * - Ensures adherence to external API limits by introducing delays between batch processing if required.
     *
     * Dependencies:
     * - This method relies on other services and repositories such as `virtualStationRepository`,
     *   `fetchBatch`, `storageService`, and `importMonitor` for performing its operations.
     *
     * Thread-safety:
     * - An {@code AtomicBoolean} is used to handle the critical error state in a thread-safe manner, which
     *   allows for coordinated handling of errors across different parts of the method.
     *
     * Edge cases:
     * - Handles the scenario where there are no active virtual stations by logging this condition and terminating early.
     * - Ensures that critical errors stop further processing to prevent redundant or erroneous operations.
     *
     * This method is suitable for periodic tasks to update the database with the latest data from external sources.
     */
    public void fetchAndSaveCurrentData() {
        log.info("START: Pobieranie danych bieżących (Light)...");

        List<VirtualStation> stations = virtualStationRepository.findAllByActiveTrue();
        if (stations.isEmpty()) {
            log.info("Brak aktywnych stacji wirtualnych.");
            return;
        }

        AtomicBoolean criticalErrorOccurred = new AtomicBoolean(false);
        int totalSaved = 0;

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
                try {
                    storageService.saveNewDataOnly(fetchedData);
                    totalSaved += fetchedData.size();
                } catch (Exception e) {
                    log.error("Błąd zapisu do bazy: {}", e.getMessage());
                }
            }

            if (i < batches.size() - 1 && !criticalErrorOccurred.get()) {
                enforceRateLimit(startBatch);
            }
        }
        importMonitor.logImport("OPEN_METEO", "CURRENT_DATA", totalSaved, criticalErrorOccurred.get());
        if (!criticalErrorOccurred.get()) {
            log.info("KONIEC: Pobieranie danych bieżących zakończone sukcesem.");
        }
    }

    /**
     * Fetches data for a batch of virtual stations by making asynchronous API requests.
     * Processes the results and maps them into VirtualStationData objects. Handles errors
     * and timeouts during processing and sets an error flag when critical issues occur.
     *
     * @param batch A list of VirtualStation objects representing the batch to process.
     * @param errorFlag An AtomicBoolean used to flag any critical errors encountered during processing.
     * @return A list of VirtualStationData objects containing the fetched and processed data for the given batch.
     *         If an error occurs during processing, it returns an empty list.
     */
    private List<VirtualStationData> fetchBatch(List<VirtualStation> batch, AtomicBoolean errorFlag) {
        List<CompletableFuture<VirtualStationData>> futures = batch.stream()
                .map(station -> CompletableFuture.supplyAsync(() -> {
                            if (errorFlag.get()) return null;

                            String url = buildUrl(station);
                            return openMeteoClient.fetchData(url, OpenMeteoLightResponse.class)
                                    .map(response -> mapper.toVirtualStationData(response, station))
                                    .orElse(null);
                        }, weatherExecutor)
                        .orTimeout(API_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                        .exceptionally(ex -> {
                            log.error("API ERROR/TIMEOUT dla stacji '{}': {}. Ustawiam flagę CRITICAL.",
                                    station.getName(), ex.getMessage());
                            errorFlag.set(true);
                            return null;
                        }))
                .toList();

        try {
            return futures.stream()
                    .map(CompletableFuture::join)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (Exception e) {
            log.error("Nieoczekiwany błąd w przetwarzaniu paczki: {}", e.getMessage());
            errorFlag.set(true);
            return Collections.emptyList();
        }
    }

    /**
     * Enforces a rate limit by pausing execution if necessary to ensure a minimum required
     * delay between two consecutive operations.
     *
     * @param startBatchTime the timestamp (in milliseconds) when the batch started processing
     */
    private void enforceRateLimit(long startBatchTime) {
        long elapsed = System.currentTimeMillis() - startBatchTime;
        long sleepTime = RATE_LIMIT_PAUSE_MS - elapsed;

        if (sleepTime > 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Przerwano oczekiwanie (Rate Limit).");
            }
        }
    }

    /**
     * Constructs a URL for the OpenMeteo API based on the provided VirtualStation's location
     * and predefined query parameters.
     *
     * @param station the virtual station containing latitude and longitude used for building the URL
     * @return the complete URL string with query parameters for the API
     */
    private String buildUrl(VirtualStation station) {
        return UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("latitude", station.getLatitude())
                .queryParam("longitude", station.getLongitude())
                .queryParam("current", API_PARAMS)
                .queryParam("timezone", TIMEZONE)
                .build()
                .toUriString();
    }

    /**
     * Divides the given list into smaller sublists, each with a maximum specified size.
     * If the total number of elements in the list is not divisible by the size,
     * the last batch will contain the remaining elements.
     *
     * @param list the list to be split into batches
     * @param size the maximum size of each batch
     * @param <T> the type of elements in the list
     * @return a list of batches, where each batch is a sublist of the given list
     *         with a size up to the specified maximum size
     */
    private <T> List<List<T>> splitIntoBatches(List<T> list, int size) {
        List<List<T>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            batches.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return batches;
    }
}