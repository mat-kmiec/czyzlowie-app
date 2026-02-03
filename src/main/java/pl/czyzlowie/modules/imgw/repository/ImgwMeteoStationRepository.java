package pl.czyzlowie.modules.imgw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.imgw.entity.ImgwMeteoStation;

@Repository
public interface ImgwMeteoStationRepository extends JpaRepository<ImgwMeteoStation, String> {}