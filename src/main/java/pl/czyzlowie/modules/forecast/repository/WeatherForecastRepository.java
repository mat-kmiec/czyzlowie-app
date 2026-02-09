package pl.czyzlowie.modules.forecast.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.forecast.entity.WeatherForecast;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherForecastRepository extends JpaRepository<WeatherForecast, Long> {


    List<WeatherForecast> findAllBySynopStationIdInAndForecastTimeBetween(
            Collection<String> stationIds, LocalDateTime start, LocalDateTime end);

    List<WeatherForecast> findAllByVirtualStationIdInAndForecastTimeBetween(
            Collection<String> stationIds, LocalDateTime start, LocalDateTime end);
}
