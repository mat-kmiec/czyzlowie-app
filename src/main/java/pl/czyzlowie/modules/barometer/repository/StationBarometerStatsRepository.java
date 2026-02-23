package pl.czyzlowie.modules.barometer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.barometer.entity.StationBarometerId;
import pl.czyzlowie.modules.barometer.entity.StationBarometerStats;

import java.util.Optional;

@Repository
public interface StationBarometerStatsRepository extends JpaRepository<StationBarometerStats, StationBarometerId> {
    boolean existsByIdStationId(String stationId);
    Optional<StationBarometerStats> findByIdStationId(String stationId);
}