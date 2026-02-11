package pl.czyzlowie.modules.forecast.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.forecast.entity.VirtualStation;

import java.util.List;

/**
 * Repository interface for managing {@code VirtualStation} entities.
 * Extends the {@code JpaRepository} interface to provide CRUD operations and additional query methods
 * for interacting with virtual weather station data.
 *
 * Functionality:
 * - Enables retrieval, creation, updating, and deletion of virtual station records.
 * - Provides custom query capabilities tailored to the {@code VirtualStation} entity, such as
 *   fetching all active virtual stations.
 *
 * Relationships:
 * - The {@code VirtualStation} entity is utilized in various domains, such as weather forecasting,
 *   data collection, and import logs.
 *
 * Query Methods:
 * - Includes specialized methods, such as fetching only "active" virtual stations in the system.
 */
@Repository
public interface VirtualStationRepository extends JpaRepository<VirtualStation, String> {


    /**
     * Retrieves a list of all active virtual weather stations.
     *
     * @return a list of {@code VirtualStation} entities that have the "active" flag set to true
     */
    List<VirtualStation> findAllByActiveTrue();

}