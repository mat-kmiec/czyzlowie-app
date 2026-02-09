package pl.czyzlowie.modules.forecast.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.forecast.entity.WeatherImportLog;
import pl.czyzlowie.modules.forecast.repository.WeatherImportLogRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WeatherImportMonitor {

    private final WeatherImportLogRepository importLogRepository;


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
