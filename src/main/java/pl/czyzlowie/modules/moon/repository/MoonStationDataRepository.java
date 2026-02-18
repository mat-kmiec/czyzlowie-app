package pl.czyzlowie.modules.moon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.czyzlowie.modules.moon.entity.MoonStationData;
import pl.czyzlowie.modules.moon.entity.MoonStationDataId;

import java.time.LocalDate;
import java.util.Set;

public interface MoonStationDataRepository extends JpaRepository<MoonStationData, MoonStationDataId>{

    @Query("SELECT m.id FROM MoonStationData m WHERE m.id.calculationDate BETWEEN :startDate AND :endDate")
    Set<MoonStationDataId> findExistingIdsBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
