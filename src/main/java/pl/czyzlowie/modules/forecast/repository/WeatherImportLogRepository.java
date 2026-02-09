package pl.czyzlowie.modules.forecast.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.czyzlowie.modules.forecast.entity.WeatherImportLog;

public interface WeatherImportLogRepository extends JpaRepository<WeatherImportLog, Long> {}
