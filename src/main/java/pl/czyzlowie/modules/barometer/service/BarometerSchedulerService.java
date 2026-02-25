package pl.czyzlowie.modules.barometer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.barometer.entity.StationType;
import pl.czyzlowie.modules.forecast.repository.VirtualStationRepository;
import pl.czyzlowie.modules.imgw_api.repository.ImgwSynopStationRepository;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service responsible for scheduling and calculating barometer statistics for weather stations.
 * Utilizes both IMGW synoptic stations and virtual weather stations to compute and save statistics.
 * The statistics calculation is performed asynchronously for efficient processing.
 *
 * Key Responsibilities:
 * - Periodically schedules the computation of barometer stats using a cron expression.
 * - Fetches data for active IMGW synoptic stations and virtual stations.
 * - Distributes computation tasks asynchronously across a provided executor.
 * - Logs progress, including success and error counts, after task execution.
 *
 * Dependencies:
 * - {@link ImgwSynopStationRepository}: Repository for accessing active IMGW synoptic stations.
 * - {@link VirtualStationRepository}: Repository for accessing active virtual weather stations.
 * - {@link BarometerEngineService}: Service responsible for the actual computation of barometer stats.
 * - Executor: Custom thread executor for parallel task execution.
 *
 * Scheduling and Execution:
 * - Automatically executed at fixed intervals (every 30 minutes) via a scheduled task with a cron expression.
 * - Triggers the computation logic when the application is fully initialized using the {@link ApplicationReadyEvent}.
 *
 * Logging:
 * - Provides detailed logging at various stages of task execution, including start, end, success, and errors.
 */
@Slf4j
@Service
public class BarometerSchedulerService {

    private final ImgwSynopStationRepository synopRepository;
    private final VirtualStationRepository virtualRepository;
    private final BarometerEngineService engineService;
    private final Executor executor;

    /**
     * Constructs a new instance of the BarometerSchedulerService.
     * Initializes the service with the required repositories, computation engine, and executor for asynchronous processing.
     *
     * @param synopRepository the repository used to interact with IMGW synoptic weather stations
     * @param virtualRepository the repository used to interact with virtual weather stations
     * @param engineService the service responsible for calculating and saving barometer statistics
     * @param executor the executor used for asynchronous computation of barometer statistics
     */
    public BarometerSchedulerService(
            ImgwSynopStationRepository synopRepository,
            VirtualStationRepository virtualRepository,
            BarometerEngineService engineService,
            @Qualifier("weatherExecutor") Executor executor) {
        this.synopRepository = synopRepository;
        this.virtualRepository = virtualRepository;
        this.engineService = engineService;
        this.executor = executor;
    }

    /**
     * Periodically calculates barometer statistics for all active weather stations asynchronously.
     * <br>
     * This method performs the following key operations:
     * - Retrieves a list of all active IMGW synoptic stations and virtual weather stations.
     * - Assembles tasks for computing statistics for each station.
     * - Executes these tasks in parallel using a configured thread executor.
     * - Monitors success and error counts during execution and logs the results.
     * <br>
     * Details:
     * - The method is triggered automatically upon application startup via {@link ApplicationReadyEvent}.
     * - It is also scheduled to run every 30 minutes using the defined cron expression.
     * <br>
     * Logging:
     * - Logs the start and end of the computation process along with the count of successfully processed stations and errors.
     * - Logs detailed error messages for individual station computation failures.
     */
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "0 0/30 * * * *")
    public void calculateAllBarometerStatsAutomated() {
        log.info("[BAROMETR-JOB] START: Asynchroniczne przeliczanie statystyk...");

        var successCount = new AtomicInteger(0);
        var errorCount = new AtomicInteger(0);
        var tasks = new ArrayList<StationTask>();
        synopRepository.findAllByIsActiveTrue()
                .forEach(s -> tasks.add(new StationTask(s.getId(), StationType.IMGW_SYNOP)));
        virtualRepository.findAllByActiveTrue()
                .forEach(s -> tasks.add(new StationTask(s.getId(), StationType.VIRTUAL)));

        var futures = tasks.stream()
                .map(task -> CompletableFuture.runAsync(() ->
                        processSingleStation(task, successCount, errorCount), executor))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).join();

        log.info("[BAROMETR-JOB] KONIEC: Przeliczono pomyślnie: {} stacji. Błędy: {}",
                successCount.get(), errorCount.get());
    }

    /**
     * Processes a single weather station task by calculating and saving its barometer statistics.
     * Updates the counters for successful and failed computations accordingly.
     *
     * @param task the task containing the information of the weather station to be processed
     * @param successCount the counter for successfully processed tasks, incremented upon successful execution
     * @param errorCount the counter for tasks that encountered errors, incremented upon failure
     */
    private void processSingleStation(StationTask task, AtomicInteger successCount, AtomicInteger errorCount) {
        try {
            engineService.calculateAndSaveStats(task.id(), task.type());
            successCount.incrementAndGet();
        } catch (Exception e) {
            log.error("[BAROMETR-JOB] Błąd przeliczania dla stacji {} ({}). Powód: {}",
                    task.type(), task.id(), e.getMessage(), e);
            errorCount.incrementAndGet();
        }
    }

    /**
     * Represents a scheduled task for processing a specific weather station.
     * Encapsulates the station's unique identifier and its type.
     *
     * Primary Use:
     * - Used to define and track tasks related to weather station calculations, particularly
     *   barometer statistics.
     *
     * Attributes:
     * - id: A unique identifier representing the specific weather station.
     * - type: The type of the station, such as IMGW synoptic or virtual.
     *
     * Design:
     * - Implemented as a record to provide an immutable and thread-safe data structure.
     *
     * Usage Context:
     * - Commonly instantiated and utilized as part of task distribution in services like
     *   {@code BarometerSchedulerService}.
     */
    private record StationTask(String id, StationType type) {}
}