package pl.czyzlowie.modules.barometer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.barometer.entity.StationBarometerId;
import pl.czyzlowie.modules.barometer.entity.StationBarometerStats;

@Repository
public interface StationBarometerStatsRepository extends JpaRepository<StationBarometerStats, StationBarometerId> {
}