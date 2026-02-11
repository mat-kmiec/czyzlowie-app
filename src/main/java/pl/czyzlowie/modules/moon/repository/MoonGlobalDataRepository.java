package pl.czyzlowie.modules.moon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.czyzlowie.modules.moon.entity.MoonGlobalData;

import java.time.LocalDate;

public interface MoonGlobalDataRepository extends JpaRepository<MoonGlobalData, LocalDate> {
}
