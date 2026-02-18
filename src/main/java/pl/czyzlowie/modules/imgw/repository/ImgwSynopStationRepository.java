package pl.czyzlowie.modules.imgw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.imgw.entity.ImgwSynopStation;
import pl.czyzlowie.modules.moon.projection.StationCoordinatesView;

import java.util.List;

@Repository
public interface ImgwSynopStationRepository extends JpaRepository<ImgwSynopStation, String> {
    List<ImgwSynopStation> findAllByIsActiveTrue();
    @Query("SELECT s.id AS id, s.latitude AS latitude, s.longitude AS longitude FROM ImgwSynopStation s WHERE s.isActive = true")
    List<StationCoordinatesView> findActiveStationCoordinates();
}