package pl.czyzlowie.modules.map.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.map.entity.MapSpot;
import pl.czyzlowie.modules.map.entity.SpotType;

import java.util.List;
import java.util.Optional;

@Repository
public interface MapSpotRepository extends JpaRepository<MapSpot, Long>, JpaSpecificationExecutor<MapSpot> {

    @Query("SELECT s FROM MapSpot s WHERE " +
            "(s.latitude BETWEEN :south AND :north AND s.longitude BETWEEN :west AND :east) " +
            "OR TYPE(s) = RestrictionSpot")
    List<MapSpot> findInBoundsOrRestrictions(
            @Param("south") Double south,
            @Param("north") Double north,
            @Param("west") Double west,
            @Param("east") Double east
    );

    Optional<MapSpot> findBySlugAndSpotType(String slug, SpotType spotType);

    Page<MapSpot> findBySpotType(SpotType spotType, Pageable pageable);
}
