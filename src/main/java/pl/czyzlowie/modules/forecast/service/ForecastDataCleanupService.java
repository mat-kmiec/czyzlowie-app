package pl.czyzlowie.modules.forecast.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.forecast.repository.VirtualStationDataRepository;
import pl.czyzlowie.modules.forecast.repository.WeatherForecastRepository;

import java.time.LocalDateTime;

/**
 * A service responsible for cleaning up outdated forecast-related data from
 * the system, specifically from the VirtualStationData and WeatherForecast repositories.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ForecastDataCleanupService {

    private final VirtualStationDataRepository virtualStationDataRepo;
    private final WeatherForecastRepository weatherForecastRepo;

    /**
     * Cleans up old forecast data that is older than the specified number of days.
     * Removes records from VirtualStationData and WeatherForecast repositories.
     *
     * @param daysToKeep the number of days of data to retain; records older than this will be deleted
     */
    public void cleanupOldData(int daysToKeep) {
        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(daysToKeep);

        log.info("Rozpoczynam sekwencyjne czyszczenie danych Prognoz (Forecast) starszych niż {}", thresholdDate);

        int vsdDeleted = virtualStationDataRepo.deleteOlderThan(thresholdDate);
        log.info("Usunięto {} starych rekordów VirtualStationData.", vsdDeleted);

        int wfDeleted = weatherForecastRepo.deleteOlderThan(thresholdDate);
        log.info("Usunięto {} starych rekordów WeatherForecast.", wfDeleted);

        log.info("Nocne czyszczenie bazy Prognoz zakończone sukcesem.");
    }
}
