package pl.czyzlowie.modules.moon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.czyzlowie.modules.moon.entity.MoonStationData;
import pl.czyzlowie.modules.moon.entity.MoonStationDataId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for accessing and managing MoonStationData entities in the database.
 * Provides various methods for querying and retrieving MoonStationData records,
 * including custom queries based on calculation dates.
 *
 * Extends JpaRepository to inherit common CRUD operations for the MoonStationData entity.
 */
public interface MoonStationDataRepository extends JpaRepository<MoonStationData, MoonStationDataId>{

    @Query("SELECT m.id FROM MoonStationData m WHERE m.id.calculationDate BETWEEN :startDate AND :endDate")
    Set<MoonStationDataId> findExistingIdsBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT m FROM MoonStationData m WHERE m.id.stationId = :stationId AND m.id.stationType = :stationType AND m.id.calculationDate BETWEEN :startDate AND :endDate ORDER BY m.id.calculationDate ASC")
    List<MoonStationData> findStationTimeline(
            @Param("stationId") String stationId,
            @Param("stationType") String stationType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    Optional<MoonStationData> findByIdStationIdAndIdCalculationDate(String stationId, LocalDate date);
}
