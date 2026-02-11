package pl.czyzlowie.modules.forecast.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.forecast.entity.WeatherImportLog;
import pl.czyzlowie.modules.forecast.repository.WeatherImportLogRepository;

import java.time.LocalDateTime;

/**
 * Service responsible for monitoring and logging weather data import operations.
 *
 * This class provides functionality to track the details of weather data imports into
 * the system by writing entries into a designated database table. It encapsulates
 * logic for recording metadata about the import process, such as data provider,
 * the type of import, the number of records, and whether the operation completed
 * successfully or encountered errors.
 *
 * Dependencies:
 * - WeatherImportLogRepository: Used to persist log entries related to weather data imports.
 */
@Service
@RequiredArgsConstructor
public class WeatherImportMonitor {

    private final WeatherImportLogRepository importLogRepository;


    /**
     * Logs the details of a weather data import operation into the system.
     *
     * This method creates a new entry in the weather import logs database,
     * capturing relevant information about the import process such as provider,
     * type, status, record count, and timestamp. The operation is executed within
     * a new transactional context.
     *
     * @param provider The name of the provider from which the weather data was imported.
     * @param type The type of import process (e.g., hourly, daily).
     * @param count The number of records imported during the process.
     * @param isError A boolean flag indicating whether the import operation resulted in an error.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logImport(String provider, String type, int count, boolean isError) {
        WeatherImportLog logEntry = WeatherImportLog.builder()
                .provider(provider)
                .importType(type)
                .status(isError ? "ERROR" : "SUCCESS")
                .recordsCount(count)
                .createdAt(LocalDateTime.now())
                .build();

        importLogRepository.save(logEntry);
    }
}
