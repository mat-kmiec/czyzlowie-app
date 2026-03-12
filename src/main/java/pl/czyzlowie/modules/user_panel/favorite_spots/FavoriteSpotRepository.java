package pl.czyzlowie.modules.user_panel.favorite_spots;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.user_panel.favorite_spots.FavoriteSpot;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing "favorite_spots" in the database.
 * Provides methods to query favorite spots data for specific users based on various criteria.
 */
@Repository
public interface FavoriteSpotRepository extends JpaRepository<FavoriteSpot, Long> {

    Page<FavoriteSpot> findAllByUserEmailOrderByCreatedAtDesc(String email, Pageable pageable);

    Optional<FavoriteSpot> findByIdAndUserEmail(Long id, String email);

    long countByUserEmail(String email);

    @Query("SELECT fs.waterType FROM FavoriteSpot fs WHERE fs.user.email = :email GROUP BY fs.waterType ORDER BY COUNT(fs) DESC")
    List<WaterType> findMostFrequentWaterTypeByUserEmail(String email, Pageable pageable);

    @Query("SELECT tag FROM FavoriteSpot fs JOIN fs.fishTags tag WHERE fs.user.email = :email GROUP BY tag ORDER BY COUNT(tag) DESC")
    List<String> findMostFrequentFishTagByUserEmail(String email, Pageable pageable);
}