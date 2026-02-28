package pl.czyzlowie.modules.moon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.czyzlowie.modules.moon.entity.MoonGlobalData;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MoonGlobalDataRepository extends JpaRepository<MoonGlobalData, LocalDate> {
    /**
     * Retrieves a set of calculation dates for which records exist within the specified date range.
     *
     * @param startDate the start date of the range (inclusive).
     * @param endDate the end date of the range (inclusive).
     * @return a set of dates that exist in the database within the specified date range.
     */
    @Query("SELECT m.calculationDate FROM MoonGlobalData m WHERE m.calculationDate BETWEEN :startDate AND :endDate")
    Set<LocalDate> findExistingDatesBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    Optional<MoonGlobalData> findByCalculationDate(LocalDate date);
    List<MoonGlobalData> findByCalculationDateBetweenOrderByCalculationDateAsc(LocalDate startDate, LocalDate endDate);
}
