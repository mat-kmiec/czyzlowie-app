package pl.czyzlowie.modules.imgw_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.imgw_api.entity.ImgwHydroData;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ImgwHydroDataRepository extends JpaRepository<ImgwHydroData, Long> {
    Optional<ImgwHydroData> findTopByStationIdOrderByIdDesc(String stationId);

    /**
     * Retrieves the latest data entries for a collection of station IDs. The latest data is determined
     * by the highest ID for each station.
     *
     * @param stationIds a collection of station IDs for which the latest data is to be fetched
     * @return a list of {@code ImgwHydroData} objects representing the latest data for the specified stations
     */
    @Query("SELECT d FROM ImgwHydroData d " +
            "WHERE d.id IN (" +
            "    SELECT MAX(d2.id) FROM ImgwHydroData d2 " +
            "    WHERE d2.station.id IN :stationIds " +
            "    GROUP BY d2.station.id" +
            ")")
    List<ImgwHydroData> findLatestDataForStations(@Param("stationIds") Collection<String> stationIds);

    List<ImgwHydroData> findByStationIdAndWaterLevelDateBetweenOrderByWaterLevelDateAsc(
            String stationId, LocalDateTime startDate, LocalDateTime endDate);


    @Query("SELECT h FROM ImgwHydroData h " +
                "WHERE h.station.id = :stationId " +
                "AND h.waterLevelDate >= :startTime " +
                "AND h.waterLevelDate <= :endTime " +
                "ORDER BY h.waterLevelDate ASC")
    List<ImgwHydroData> findByStationIdAndDateRange(
                @Param("stationId") Long stationId,
                @Param("startTime") LocalDateTime startTime,
                @Param("endTime") LocalDateTime endTime
        );
}