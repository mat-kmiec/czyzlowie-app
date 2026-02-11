package pl.czyzlowie.modules.forecast.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.czyzlowie.modules.forecast.entity.WeatherImportLog;

/**
 * Repository interface for managing {@link WeatherImportLog} entities.
 * Extends the {@link JpaRepository} to provide CRUD operations and database interactions.
 *
 * This interface enables operations such as saving, updating, deleting, and finding
 * {@link WeatherImportLog} entities in the "weather_import_logs" table.
 *
 * Key functionalities include:
 * - Simplified access to the database for managing import log entries.
 * - Support for standard JPA query methods and additional custom queries if needed.
 */
public interface WeatherImportLogRepository extends JpaRepository<WeatherImportLog, Long> {}
