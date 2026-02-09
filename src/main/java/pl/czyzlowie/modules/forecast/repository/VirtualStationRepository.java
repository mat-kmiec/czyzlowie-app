package pl.czyzlowie.modules.forecast.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.forecast.entity.VirtualStation;

import java.util.List;

@Repository
public interface VirtualStationRepository extends JpaRepository<VirtualStation, String> {


    List<VirtualStation> findAllByActiveTrue();

}