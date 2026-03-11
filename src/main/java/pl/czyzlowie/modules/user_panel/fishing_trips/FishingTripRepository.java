package pl.czyzlowie.modules.user_panel.fishing_trips;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing FishingTrip entities.
 * Provides methods to interact with the FishingTrip data in the database,
 * including custom queries for specific operations.
 */
@Repository
public interface FishingTripRepository extends JpaRepository<FishingTrip, Long> {

    Page<FishingTrip> findAllByUserIdOrderByStartDateDesc(Long userId, Pageable pageable);

    Optional<FishingTrip> findByIdAndUserId(Long tripId, Long userId);

    @Query("SELECT COALESCE(SUM(t.caughtFish), 0) FROM FishingTrip t WHERE t.user.id = :userId")
    Integer sumCaughtFishByUserId(@Param("userId") Long userId);

    @Query("SELECT t.fishingType FROM FishingTrip t WHERE t.user.id = :userId GROUP BY t.fishingType ORDER BY COUNT(t) DESC")
    List<FishingType> findFavoriteFishingTypes(@Param("userId") Long userId);
}
