package pl.czyzlowie.modules.forecast.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.forecast.entity.VirtualStation;
import pl.czyzlowie.modules.moon.projection.StationCoordinatesView;

import java.util.List;

/**
 * Repository interface for managing {@link VirtualStation} entities in the database.
 * Extends the {@link JpaRepository} to provide basic CRUD operations and additional
 * query methods specific to virtual weather stations.
 *
 * Functionalities:
 * - Allows fetching all active virtual weather stations.
 * - Provides custom queries for retrieving specific attributes of active stations, such as coordinates.
 *
 * Key Features:
 * - Interaction with the "virtual_stations" table for querying, saving, updating, or deleting
 *   {@link VirtualStation} entities.
 * - Supports integration with projections, such as {@link StationCoordinatesView}, to simplify data retrieval.
 *
 * Relationships:
 * - The {@link VirtualStation} entity holds key information about weather data sources,
 *   used across forecast and data import processes.
 */
@Repository
public interface VirtualStationRepository extends JpaRepository<VirtualStation, String> {


    /**
     * Retrieves a list of all active virtual weather stations.
     * @return a list of {@code VirtualStation} entities that have the "active" flag set to true
     */
    List<VirtualStation> findAllByActiveTrue();

    /**
     * Retrieves a list of coordinates (latitude, longitude) and IDs of all active virtual stations.
     * Active stations are defined as those with the "active" flag set to true.
     *
     * @return a list of {@code StationCoordinatesView} projections containing IDs, latitude, and longitude
     *         of active virtual stations.
     */
    @Query("SELECT v.id AS id, v.latitude AS latitude, v.longitude AS longitude FROM VirtualStation v WHERE v.active = true")
    List<StationCoordinatesView> findActiveStationCoordinates();

}