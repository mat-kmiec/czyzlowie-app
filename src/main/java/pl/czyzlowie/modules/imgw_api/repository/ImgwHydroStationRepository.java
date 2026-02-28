package pl.czyzlowie.modules.imgw_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.imgw_api.entity.ImgwHydroStation;
import pl.czyzlowie.modules.moon.projection.StationCoordinatesView;

import java.util.List;

@Repository
public interface ImgwHydroStationRepository extends JpaRepository<ImgwHydroStation, String> {

    @Query("SELECT s FROM ImgwHydroStation s WHERE s.latitude BETWEEN :south AND :north AND s.longitude BETWEEN :west AND :east AND s.isActive = true")
    List<ImgwHydroStation> findInBounds(
            @Param("south") Double south,
            @Param("north") Double north,
            @Param("west") Double west,
            @Param("east") Double east
    );

    @Query("SELECT s.id AS id, s.latitude AS latitude, s.longitude AS longitude FROM ImgwHydroStation s WHERE s.isActive = true")
    List<StationCoordinatesView> findActiveStationCoordinates();
}