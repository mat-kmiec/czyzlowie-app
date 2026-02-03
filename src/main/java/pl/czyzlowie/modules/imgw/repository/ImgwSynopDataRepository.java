package pl.czyzlowie.modules.imgw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.imgw.entity.ImgwSynopData;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ImgwSynopDataRepository extends JpaRepository<ImgwSynopData, Long> {

    boolean existsByStationIdAndMeasurementDateAndMeasurementHour(
            String stationId,
            LocalDate measurementDate,
            Integer measurementHour
    );

    Optional<ImgwSynopData> findTopByStationIdOrderByIdDesc(String stationId);

    @Query("SELECT d FROM ImgwSynopData d " +
            "WHERE d.id IN (" +
            "    SELECT MAX(d2.id) FROM ImgwSynopData d2 " +
            "    WHERE d2.station.id IN :stationIds " +
            "    GROUP BY d2.station.id" +
            ")")
    List<ImgwSynopData> findLatestDataForStations(@Param("stationIds") Collection<String> stationIds);
}
