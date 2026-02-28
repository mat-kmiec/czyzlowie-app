package pl.czyzlowie.modules.imgw_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.imgw_api.entity.ImgwMeteoData;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ImgwMeteoDataRepository extends JpaRepository<ImgwMeteoData, Long> {
    Optional<ImgwMeteoData> findTopByStationIdOrderByIdDesc(String stationId);

    @Query("SELECT d FROM ImgwMeteoData d " +
            "WHERE d.id IN (" +
            "    SELECT MAX(d2.id) FROM ImgwMeteoData d2 " +
            "    WHERE d2.station.id IN :stationIds " +
            "    GROUP BY d2.station.id" +
            ")")
    List<ImgwMeteoData> findLatestDataForStations(@Param("stationIds") Collection<String> stationIds);

    @Query("SELECT m FROM ImgwMeteoData m " +
            "WHERE m.station.id = :stationId " +
            "AND m.createdAt >= :startTime " +
            "AND m.createdAt <= :endTime " +
            "ORDER BY m.createdAt ASC")
    List<ImgwMeteoData> findHistoryForForecast(
            @Param("stationId") Long stationId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );


    List<ImgwMeteoData> findByStationIdAndCreatedAtBetweenOrderByCreatedAtAsc(String stationId, LocalDateTime start, LocalDateTime end);
}
