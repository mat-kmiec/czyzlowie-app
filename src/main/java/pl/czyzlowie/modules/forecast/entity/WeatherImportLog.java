package pl.czyzlowie.modules.forecast.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents an entity that logs weather data import processes.
 * This class is mapped to the "weather_import_logs" table in the database
 * and is used to track details regarding weather data imports, including
 * the provider, type of import, status, record count, and error messages,
 * if applicable.
 *
 * Fields:
 * - id: The unique identifier of the log entry.
 * - provider: The name of the provider from which the weather data was imported.
 * - importType: The type of the import process (e.g., hourly, daily).
 * - status: The status of the import process (e.g., success, failure).
 * - recordsCount: The number of records imported during the process.
 * - errorMessage: Details about any errors encountered during the import, if applicable.
 * - createdAt: The timestamp indicating when the log entry was created.
 *
 * The WeatherImportLog class is primarily intended for tracking and managing
 * import operations in the system, providing insights into the performance
 * and potential issues in the data import process.
 */
@Entity
@Table(name = "weather_import_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherImportLog {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String importType;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Integer recordsCount;
    private String errorMessage;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
