package pl.czyzlowie.modules.forecast.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.barometer.dto.ForecastPressurePoint;
import pl.czyzlowie.modules.forecast.entity.WeatherForecast;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Repository interface for managing {@code WeatherForecast} entities. It provides methods for
 * retrieving, querying, and deleting weather forecast data based on various filters such as
 * station IDs, timestamps, and time ranges. This repository extends {@code JpaRepository},
 * enabling CRUD operations and custom query execution for the {@code WeatherForecast} entity.
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

    /**
     * Retrieves a list of forecasted pressure points for a virtual weather station, ordered by forecast time.
     *
     * @param stationId the unique identifier of the virtual station for which the forecast is requested
     * @param now the current time; forecasts will only include entries with times equal to or after this timestamp
     * @return a list of {@code ForecastPressurePoint} containing the forecast time and pressure values
     */
    @Query("SELECT f.forecastTime AS forecastTime, f.pressure AS pressure " +
            "FROM WeatherForecast f " +
            "WHERE f.virtualStation.id = :stationId " +
            "AND f.forecastTime >= :now " +
            "ORDER BY f.forecastTime ASC")
    List<ForecastPressurePoint> findVirtualPressureForecast(@Param("stationId") String stationId, @Param("now") LocalDateTime now);


    /**
     * Retrieves a list of weather forecasts for a specified synoptic station within a given time range.
     *
     * @param stationId the ID of the synoptic station for which the forecast is to be retrieved
     * @param startTime the start of the time range for which the forecast is to be retrieved
     * @param endTime the end of the time range for which the forecast is to be retrieved
     * @return a list of {@code WeatherForecast} objects ordered by forecast time in ascending order,
     *         representing the forecasts for the specified station and time range
     */
    @Query("SELECT f FROM WeatherForecast f WHERE f.synopStation.id = :stationId AND f.forecastTime >= :startTime AND f.forecastTime <= :endTime ORDER BY f.forecastTime ASC")
    List<WeatherForecast> findForecastForImgw(@Param("stationId") Long stationId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * Retrieves a list of weather forecasts for a specific virtual station
     * within a given time range. The results are ordered by forecast time
     * in ascending order.
     *
     * @param stationId the unique identifier of the virtual station for which
     *                  the forecasts are to be retrieved
     * @param startTime the start of the time range for the forecast retrieval
     * @param endTime   the end of the time range for the forecast retrieval
     * @return a list of {@code WeatherForecast} objects that match the specified
     *         virtual station and fall within the given time range
     */
    @Query("SELECT f FROM WeatherForecast f WHERE f.virtualStation.id = :stationId AND f.forecastTime >= :startTime AND f.forecastTime <= :endTime ORDER BY f.forecastTime ASC")
    List<WeatherForecast> findForecastForVirtual(@Param("stationId") String stationId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * Deletes records from the WeatherForecast table where the fetchedAt timestamp is older than the specified threshold date.
     *
     * @param thresholdDate the LocalDateTime threshold; any records with a fetchedAt timestamp older than this date will be deleted.
     * @return the number of rows affected by the delete query.
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM WeatherForecast d WHERE d.fetchedAt < :thresholdDate")
    int deleteOlderThan(@Param("thresholdDate") LocalDateTime thresholdDate);
}
