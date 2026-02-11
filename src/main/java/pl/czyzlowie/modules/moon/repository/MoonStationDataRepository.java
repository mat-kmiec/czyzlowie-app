package pl.czyzlowie.modules.moon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.czyzlowie.modules.moon.entity.MoonStationData;
import pl.czyzlowie.modules.moon.entity.MoonStationDataId;

public interface MoonStationDataRepository extends JpaRepository<MoonStationData, MoonStationDataId>{
}
