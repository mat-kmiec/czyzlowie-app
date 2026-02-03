package pl.czyzlowie.modules.imgw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.imgw.entity.ImgwHydroStation;

@Repository
public interface ImgwHydroStationRepository extends JpaRepository<ImgwHydroStation, String> {}