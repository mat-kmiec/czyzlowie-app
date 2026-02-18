package pl.czyzlowie.modules.moon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.czyzlowie.modules.moon.entity.MoonStationData;
import pl.czyzlowie.modules.moon.entity.MoonStationDataId;

import java.time.LocalDate;
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
}
