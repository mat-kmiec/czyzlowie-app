package pl.czyzlowie.modules.moon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.czyzlowie.modules.moon.entity.MoonData;
import pl.czyzlowie.modules.moon.entity.MoonRegion;

import java.time.LocalDate;

public interface MoonDataRepository extends JpaRepository<MoonData, Long> {
    boolean existsByDateAndRegionNode(LocalDate date, MoonRegion regionNode);
}