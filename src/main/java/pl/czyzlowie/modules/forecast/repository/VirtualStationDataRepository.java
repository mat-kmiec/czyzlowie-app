package pl.czyzlowie.modules.forecast.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.czyzlowie.modules.barometer.dto.ForecastPressurePoint;
import pl.czyzlowie.modules.forecast.entity.VirtualStationData;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * VirtualStationDataRepository is a repository interface for managing VirtualStationData entities.
 * It extends JpaRepository, providing standard methods for CRUD operations and enabling
 * the use of custom queries to retrieve domain-specific data.
 *
 * This repository is responsible for accessing and manipulating the meteorological data
 * associated with virtual weather stations, including data retrieved based on
 * specific properties like virtual station IDs and measurement times.
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
}