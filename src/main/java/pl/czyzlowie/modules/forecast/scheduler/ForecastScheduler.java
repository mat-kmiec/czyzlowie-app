package pl.czyzlowie.modules.forecast.scheduler;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.forecast.service.VirtualStationDataService;
import pl.czyzlowie.modules.forecast.service.WeatherForecastDataService;

@Component
@RequiredArgsConstructor
@Slf4j
public class ForecastScheduler {

    private final WeatherForecastDataService forecastService;
    private final VirtualStationDataService currentDataService;

    /**
     * A scheduled method that triggers the periodic update of long-term weather forecasts.
     *
     * This method is executed at specific times defined by the cron expression
     * "0 15 0,6,12,18 * * *", which means it runs at 12:15 AM, 6:15 AM, 12:15 PM,
     * and 6:15 PM every day. Its primary function is to ensure that all weather
     * forecasts for active stations are updated on a regular basis.
     *
     * Operations performed by this method:
     * - Logs the initiation of the periodic forecast update process.
     * - Invokes the `updateAllForecasts` method of the `forecastService` to
     *   perform the updates for both synoptic and virtual stations.
     * - Handles any exceptions that may occur during the forecast update process and
     *   logs an error message along with the associated exception details.
     *
     * This scheduler ensures that the data remains current and accurate by
     * automating the process of retrieving and updating weather forecasts.
     */
    @Scheduled(cron = "0 15 0,6,12,18 * * *")
    public void scheduleForecastUpdate() {
        log.info("SCHEDULER: Rozpoczynam cykliczną aktualizację prognoz długoterminowych.");
        try {
            forecastService.updateAllForecasts();
        } catch (Exception e) {
            log.error("SCHEDULER ERROR: Błąd podczas aktualizacji prognoz: {}", e.getMessage());
        }
    }


    /**
     * A scheduled method that triggers the periodic update of current weather conditions.
     *
     * This method is executed according to the cron expression "0 5 * * * *", which means it
     * runs at the 5th second of every minute. It performs the following operations:
     *
     * - Logs the start of the update process for current weather conditions.
     * - Calls the `fetchAndSaveCurrentData` method of the `currentDataService` to fetch
     *   and store the latest weather data for active virtual stations.
     * - Catches any exceptions that occur during the execution of the data retrieval process
     *   and logs an appropriate error message with the exception details.
     *
     * Logging:
     * - Logs the start of the scheduling operation at each execution.
     * - Logs an error message alongside the exception details if an error occurs during
     *   the data fetching process.
     *
     * Dependencies:
     * - This method depends on the `currentDataService` to carry out fetching and saving of
     *   current weather data.
     * - Utilizes the `log` object for logging information and errors.
     *
     * Cron Schedule:
     * - The cron expression ensures that this method is executed periodically to maintain
     *   up-to-date data for active virtual stations.
     */
    @Scheduled(cron = "0 5 * * * *")
    public void scheduleCurrentDataUpdate() {
        log.info("SCHEDULER: Rozpoczynam pobieranie aktualnych warunków pogodowych.");
        try {
            currentDataService.fetchAndSaveCurrentData();
        } catch (Exception e) {
            log.error("SCHEDULER ERROR: Błąd podczas pobierania danych bieżących: {}", e.getMessage());
        }
    }
}
