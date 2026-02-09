package pl.czyzlowie.modules.forecast.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.czyzlowie.modules.forecast.entity.VirtualStationData;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface VirtualStationDataRepository extends JpaRepository<VirtualStationData, Long> {

    List<VirtualStationData> findAllByVirtualStationIdInAndMeasurementTimeIn(
            Collection<String> stationIds,
            Collection<LocalDateTime> measurementTimes
    );
}