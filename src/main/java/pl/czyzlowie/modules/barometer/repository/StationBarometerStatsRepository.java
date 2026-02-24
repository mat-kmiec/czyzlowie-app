package pl.czyzlowie.modules.barometer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.barometer.entity.StationBarometerId;
import pl.czyzlowie.modules.barometer.entity.StationBarometerStats;

import java.util.Optional;

/**
 * Repository interface for managing persistence and retrieval of StationBarometerStats entities.
 * Extends the JpaRepository interface to provide standard CRUD operations.
 *
 * This interface also declares custom query methods to facilitate specific data access patterns
 * related to StationBarometerStats.
 */
@Repository
public interface StationBarometerStatsRepository extends JpaRepository<StationBarometerStats, StationBarometerId> {

    /**
     * Retrieves an Optional containing the StationBarometerStats entity associated with the given station ID.
     *
     * @param stationId the ID of the station for which to retrieve the barometer statistics
     * @return an Optional containing the StationBarometerStats if a match is found, or an empty Optional if no match exists
     */
    Optional<StationBarometerStats> findByIdStationId(String stationId);
}