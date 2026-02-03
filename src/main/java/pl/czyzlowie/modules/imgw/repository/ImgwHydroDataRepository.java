package pl.czyzlowie.modules.imgw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.imgw.entity.ImgwHydroData;

import java.util.Optional;

@Repository
public interface ImgwHydroDataRepository extends JpaRepository<ImgwHydroData, Long> {
    Optional<ImgwHydroData> findTopByStationIdOrderByIdDesc(String stationId);
}