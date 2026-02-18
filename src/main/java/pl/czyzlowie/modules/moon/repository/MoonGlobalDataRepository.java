package pl.czyzlowie.modules.moon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.czyzlowie.modules.moon.entity.MoonGlobalData;

import java.time.LocalDate;
import java.util.Set;

public interface MoonGlobalDataRepository extends JpaRepository<MoonGlobalData, LocalDate> {
    @Query("SELECT m.calculationDate FROM MoonGlobalData m WHERE m.calculationDate BETWEEN :startDate AND :endDate")
    Set<LocalDate> findExistingDatesBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
