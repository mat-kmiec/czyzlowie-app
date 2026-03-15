package pl.czyzlowie.modules.user_panel.fishing_goals;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing UserGoalProgress entities.
 * Provides methods for performing database operations and custom queries
 * related to user progress on fishing goals.
 *
 * Methods:
 * - findByUserIdAndGoalId: Retrieves an optional UserGoalProgress entity
 *   by a specific user ID and goal ID.
 * - findAllByUserIdAndGoalIdIn: Retrieves a list of UserGoalProgress entities
 *   for a specific user ID and a list of goal IDs.
 */
@Repository
public interface UserGoalProgressRepository extends JpaRepository<UserGoalProgress, Long> {
    Optional<UserGoalProgress> findByUserIdAndGoalId(Long userId, Long goalId);

    List<UserGoalProgress> findAllByUserIdAndGoalIdIn(Long userId, List<Long> goalIds);
}
