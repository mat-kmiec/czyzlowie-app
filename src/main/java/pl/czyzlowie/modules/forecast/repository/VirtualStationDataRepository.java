package pl.czyzlowie.modules.forecast.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.forecast.entity.VirtualStation;
import pl.czyzlowie.modules.forecast.entity.VirtualStationData;

import java.time.LocalDateTime;
import java.util.Set;

public interface VirtualStationDataRepository extends JpaRepository<VirtualStationData, Long> {

    @Query("SELECT v.virtualStation.id FROM VirtualStationData v WHERE v.measurementTime = :time")
    Set<String> findStationIdsByMeasurementTime(LocalDateTime time);
}