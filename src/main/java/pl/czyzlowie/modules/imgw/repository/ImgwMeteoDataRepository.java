package pl.czyzlowie.modules.imgw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.imgw.entity.ImgwMeteoData;

import java.util.Optional;

@Repository
public interface ImgwMeteoDataRepository extends JpaRepository<ImgwMeteoData, Long> {
    Optional<ImgwMeteoData> findTopByStationIdOrderByIdDesc(String stationId);
}
