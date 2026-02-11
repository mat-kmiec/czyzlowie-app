package pl.czyzlowie.modules.forecast.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.forecast.entity.WeatherForecast;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link WeatherForecast} entities in the database.
 * Extends the {@link JpaRepository} to provide basic CRUD operations and additional
 * query methods specific to weather forecasts.
 *
 * Query Methods:
 * - Allows retrieving weather forecasts filtered by associated synoptic station IDs and
 *   forecast time range.
 * - Supports fetching forecasts based on virtual station IDs and specified time intervals.
 *
 * Features:
 * - Provides a way to interact with the underlying database for querying, saving,
 *   deleting, and updating {@link WeatherForecast} data.
 * - Commonly used to fetch weather forecasts for specific stations and times.
 *
 * Relationships:
 * - The {@link WeatherForecast} entity links to synoptic and virtual weather stations
 *   as part of its attributes, facilitating related queries.
 */
@Repository
public interface WeatherForecastRepository extends JpaRepository<WeatherForecast, Long> {


    /**
     * Retrieves a list of weather forecasts associated with specific synoptic station IDs
     * within a given time range.
     *
     * @param stationIds the collection of synoptic station IDs to filter forecasts by
     * @param start the start of the time range to include forecasts (inclusive)
     * @param end the end of the time range to include forecasts (exclusive)
     * @return a list of {@code WeatherForecast} entities matching the specified station IDs
     *         and time range
     */
    List<WeatherForecast> findAllBySynopStationIdInAndForecastTimeBetween(
            Collection<String> stationIds, LocalDateTime start, LocalDateTime end);

    /**
     * Retrieves a list of weather forecasts associated with specific virtual station IDs
     * within a given time range.
     *
     * @param stationIds the collection of virtual station IDs to filter forecasts by
     * @param start the start of the time range to include forecasts (inclusive)
     * @param end the end of the time range to include forecasts (exclusive)
     * @return a list of {@code WeatherForecast} entities matching the specified virtual station IDs
     *         and time range
     */
    List<WeatherForecast> findAllByVirtualStationIdInAndForecastTimeBetween(
            Collection<String> stationIds, LocalDateTime start, LocalDateTime end);
}
