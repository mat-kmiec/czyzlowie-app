package pl.czyzlowie.modules.imgw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.imgw.entity.ImgwHydroData;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ImgwHydroDataRepository extends JpaRepository<ImgwHydroData, Long> {
    Optional<ImgwHydroData> findTopByStationIdOrderByIdDesc(String stationId);

    @Query("SELECT d FROM ImgwHydroData d " +
            "WHERE d.id IN (" +
            "    SELECT MAX(d2.id) FROM ImgwHydroData d2 " +
            "    WHERE d2.station.id IN :stationIds " +
            "    GROUP BY d2.station.id" +
            ")")
    List<ImgwHydroData> findLatestDataForStations(@Param("stationIds") Collection<String> stationIds);
}