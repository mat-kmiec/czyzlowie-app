package pl.czyzlowie.modules.map.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.map.entity.MapSpot;

import java.util.Optional;

@Repository
public interface MapSpotRepository extends JpaRepository<MapSpot, Long> {

}
