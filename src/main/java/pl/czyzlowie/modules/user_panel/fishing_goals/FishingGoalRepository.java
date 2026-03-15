package pl.czyzlowie.modules.user_panel.fishing_goals;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing FishingGoal entities.
 * Extends JpaRepository to provide CRUD operations and
 * custom queries for retrieving fishing goals based on specific criteria.
 */
@Repository
public interface FishingGoalRepository extends JpaRepository<FishingGoal, Long> {

    /**
     * Retrieves a paginated list of active fishing goals for a specific user.
     * Active fishing goals include those that are either globally visible
     * or private goals created by the given user, and that have not been completed
     * or have no progress recorded.
     *
     * @param userId the ID of the user for whom the active goals are being retrieved
     * @param pageable the pagination information specifying page number, size, and sort order
     * @return a paginated list of {@link FishingGoal} entities representing the active goals for the user
     */
    @Query("SELECT g FROM FishingGoal g LEFT JOIN UserGoalProgress p ON p.goal = g AND p.userId = :userId " +
            "WHERE (g.visibility = 'GLOBAL' OR (g.visibility = 'PRIVATE' AND g.creatorId = :userId)) " +
            "AND (p IS NULL OR p.isCompleted = false)")
    Page<FishingGoal> findActiveGoalsForUser(@Param("userId") Long userId, Pageable pageable);

    /**
     * Retrieves a paginated list of completed fishing goals for a specific user.
     * Completed goals are those that have been marked as completed in the user's progress.
     *
     * @param userId the ID of the user for whom the completed goals are being retrieved
     * @param pageable the pagination information specifying page number, size, and sort order
     * @return a paginated list of {@link FishingGoal} entities representing the completed goals for the user
     */
    @Query("SELECT g FROM FishingGoal g JOIN UserGoalProgress p ON p.goal = g AND p.userId = :userId " +
            "WHERE p.isCompleted = true")
    Page<FishingGoal> findCompletedGoalsForUser(@Param("userId") Long userId, Pageable pageable);
}

