package pl.czyzlowie.modules.forecast.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.barometer.dto.ForecastPressurePoint;
import pl.czyzlowie.modules.forecast.entity.WeatherForecast;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Repository interface for accessing and managing {@code WeatherForecast} entities in the database.
 * Provides methods for querying weather forecasts based on various criteria such as station ID,
 * time ranges, and specific weather attributes. Extends {@code JpaRepository} to leverage built-in
 * CRUD operations and JPA query support.
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

    /**
     * Retrieves a list of forecasted pressure values and their corresponding timestamps
     * for a specific synoptic station, starting from the current time or later.
     *
     * @param stationId the ID of the synoptic station for which the forecasted pressure
     *                  values are to be retrieved
     * @param now the timestamp defining the starting point for retrieving forecasts;
     *            only forecasts occurring at or after this time will be included
     * @return a list of {@code ForecastPressurePoint} containing the forecasted pressure
     *         values and their corresponding timestamps, ordered by forecast time in ascending order
     */
    @Query("SELECT f.forecastTime AS forecastTime, f.pressure AS pressure " +
            "FROM WeatherForecast f " +
            "WHERE f.synopStation.id = :stationId " +
            "AND f.forecastTime >= :now " +
            "ORDER BY f.forecastTime ASC")
    List<ForecastPressurePoint> findPressureForecast(@Param("stationId") String stationId, @Param("now") LocalDateTime now);

}
