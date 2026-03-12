package pl.czyzlowie.modules.forecast.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.barometer.dto.ForecastPressurePoint;
import pl.czyzlowie.modules.forecast.entity.VirtualStationData;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing and querying VirtualStationData entities.
 * Provides methods for retrieving, querying, and performing operations on virtual weather station data.
 * Extends JpaRepository to provide standard CRUD operations and custom query methods.
 */
public interface VirtualStationDataRepository extends JpaRepository<VirtualStationData, Long> {

    /**
     * Retrieves a list of VirtualStationData entities that match the given virtual station IDs
     * and measurement times.
     *
     * @param stationIds a collection of virtual station IDs used to filter the data
     * @param measurementTimes a collection of measurement times used to filter the data
     * @return a list of VirtualStationData objects matching the specified station IDs and
     *         measurement times
     */
    List<VirtualStationData> findAllByVirtualStationIdInAndMeasurementTimeIn(
            Collection<String> stationIds,
            Collection<LocalDateTime> measurementTimes
    );

    @Query("SELECT v.measurementTime AS forecastTime, v.pressure AS pressure " +
            "FROM VirtualStationData v WHERE v.virtualStation.id = :stationId " +
            "AND v.measurementTime >= :start AND v.measurementTime <= :end " +
            "ORDER BY v.measurementTime ASC")
    List<ForecastPressurePoint> findPressureHistory(@Param("stationId") String stationId,
                                                    @Param("start") LocalDateTime start,
                                                    @Param("end") LocalDateTime end);

    /**
     * Retrieves a list of VirtualStationData entities for a specific virtual station,
     * filtered by a range of measurement times and ordered in ascending time order.
     *
     * @param virtualStationId the ID of the virtual station for which data is to be retrieved
     * @param start the start of the measurement time range (inclusive)
     * @param end the end of the measurement time range (inclusive)
     * @return a list of VirtualStationData objects that match the virtual station ID, fall within the specified
     *         time range, and are ordered by measurement time in ascending order
     */
    List<VirtualStationData> findByVirtualStationIdAndMeasurementTimeBetweenOrderByMeasurementTimeAsc(
            String virtualStationId,
            LocalDateTime start,
            LocalDateTime end
    );

    /**
     * Retrieves the most recent VirtualStationData entry for a specific virtual station,
     * where the measurement time is less than or equal to the specified date.
     * The result is ordered by measurement time in descending order, returning the first entry.
     *
     * @param virtualStationId the ID of the virtual station for which data is to be fetched
     * @param date the maximum measurement time to filter by (inclusive)
     * @return an Optional containing the most recent VirtualStationData matching the criteria,
     *         or an empty Optional if no match is found
     */
    Optional<VirtualStationData> findFirstByVirtualStationIdAndMeasurementTimeLessThanEqualOrderByMeasurementTimeDesc(Long virtualStationId, LocalDateTime date);

    /**
     * Retrieves a list of VirtualStationData entities for a specified virtual station
     * within a given time range, ordered by measurement time in ascending order.
     *
     * @param stationId the ID of the virtual station for which data is to be retrieved
     * @param startTime the start of the time range (inclusive)
     * @param endTime the end of the time range (inclusive)
     * @return a list of VirtualStationData objects that match the virtual station ID,
     *         fall within the specified time range, and are sorted in ascending order by measurement time
     */
    @Query("SELECT v FROM VirtualStationData v WHERE v.virtualStation.id = :stationId AND v.measurementTime >= :startTime AND v.measurementTime <= :endTime ORDER BY v.measurementTime ASC")
    List<VirtualStationData> findHistory(@Param("stationId") String stationId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * Deletes all records of VirtualStationData from the database where the "fetchedAt" timestamp
     * is earlier than the specified threshold date.
     *
     * @param thresholdDate the cutoff date and time; all entries with a "fetchedAt" value
     *                      earlier than this will be deleted
     * @return the number of records that were deleted from the database
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM VirtualStationData d WHERE d.fetchedAt < :thresholdDate")
    int deleteOlderThan(@Param("thresholdDate") LocalDateTime thresholdDate);
}