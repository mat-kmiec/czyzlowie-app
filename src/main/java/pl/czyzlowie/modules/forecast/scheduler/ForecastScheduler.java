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

    @Scheduled(cron = "0 15 0,6,12,18 * * *")
    public void scheduleForecastUpdate() {
        log.info("SCHEDULER: Rozpoczynam cykliczną aktualizację prognoz długoterminowych.");
        try {
            forecastService.updateAllForecasts();
        } catch (Exception e) {
            log.error("SCHEDULER ERROR: Błąd podczas aktualizacji prognoz: {}", e.getMessage());
        }
    }


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
