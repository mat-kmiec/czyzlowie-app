package pl.czyzlowie.modules.forecast.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.forecast.service.ForecastDataCleanupService;

/**
 * The ForecastCleanupScheduler class is responsible for scheduling and executing
 * periodic cleanup operations to remove outdated forecast data from the database.
 *
 * This scheduler utilizes a Spring-managed task execution mechanism to ensure
 * that older forecast data is regularly and consistently removed, thereby maintaining
 * an optimized database state.
 *
 * Responsibilities:
 * - Defines a scheduled task that executes cleanup for forecast data.
 * - Leverages the ForecastDataCleanupService to perform the cleanup logic.
 *
 * Key Behavior:
 * - The cleanup operation targets forecast data older than a predefined threshold (e.g., 7 days).
 * - Two scheduling annotations are used:
 *   1. @Scheduled(cron = "0 0 2 * * *"): Executes the cleanup process daily at 2:00 AM.
 *   2. @Scheduled(initialDelay = 3000, fixedDelay = 86400000): Executes the process with an
 *      initial delay of 3 seconds and a periodic fixed delay of 24 hours.
 *
 * Scheduling Configuration:
 * - The cleanup task is triggered based on a cron expression as well as a fixed delay mechanism,
 *   ensuring flexibility in execution timing.
 *
 * Dependencies:
 * - ForecastDataCleanupService: Performs the core logic required to delete obsolete forecast data.
 *
 * Thread Safety:
 * - This class relies on Spring's thread-safe scheduling infrastructure for synchronization and
 *   proper task execution across instances.
 *
 * Logging:
 * - Cleanup-related information and any potential errors encountered during the cleanup process
 *   are expected to be logged by the ForecastDataCleanupService.
 */
@Component
@RequiredArgsConstructor
public class ForecastCleanupScheduler {

    private final ForecastDataCleanupService cleanupService;


    /**
     * A scheduled method for cleaning up outdated forecast data from the database.
     *
     * This method is managed by the Spring Scheduler and is configured to execute
     * periodically based on the specified scheduling parameters:
     *
     * - The `@Scheduled(cron = "0 0 2 * * *")` annotation ensures that this method is
     *   executed daily at 2:00 AM.
     * - The `@Scheduled(initialDelay = 3000, fixedDelay = 86400000)` annotation provides
     *   an additional scheduling configuration where the method runs with an initial
     *   delay of 3 seconds and subsequently at fixed intervals of 24 hours (86400000 milliseconds).
     *
     * During execution, this method invokes the `cleanupOldData` method of the
     * `ForecastDataCleanupService` class, passing a parameter (e.g., 7 days) to specify
     * the retention period for forecast data. Any data older than the retention period
     * will be permanently deleted from the database.
     *
     * Key operations:
     * - Removes outdated records from the `VirtualStationData` and `WeatherForecast` repositories.
     * - Logs the initiation, progress, and completion of the cleanup process, including the count
     *   of records removed.
     *
     * This cleanup operation ensures the database is kept optimized by removing unnecessary
     * historical data, improving performance and reducing storage usage.
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void scheduleDataCleanup() {
        cleanupService.cleanupOldData(7);
    }
}
