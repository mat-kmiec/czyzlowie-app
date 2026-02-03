package pl.czyzlowie.modules.imgw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.imgw.entity.ImgwSynopData;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ImgwSynopDataRepository extends JpaRepository<ImgwSynopData, Long> {

    boolean existsByStationIdAndMeasurementDateAndMeasurementHour(
            String stationId,
            LocalDate measurementDate,
            Integer measurementHour
    );

    Optional<ImgwSynopData> findTopByStationIdOrderByIdDesc(String stationId);
}