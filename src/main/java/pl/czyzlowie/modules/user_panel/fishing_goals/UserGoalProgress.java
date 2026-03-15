package pl.czyzlowie.modules.user_panel.fishing_goals;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents the progress of a user's fishing goal.
 * This entity is mapped to the "user_goal_progress" table in the database.
 * It tracks the current progress, completion status, and other metadata
 * related to a user's specific goal.
 *
 * Fields:
 * - id: Unique identifier for the user's goal progress record.
 * - userId: Identifier of the user associated with this progress record.
 * - goal: The fishing goal to which this progress is related.
 * - currentValue: The user's current progress value towards completing the goal.
 * - isCompleted: A boolean flag indicating whether the goal has been completed.
 * - completedAt: The timestamp indicating when the goal was completed, if applicable.
 * - updatedAt: The timestamp of the most recent update to this progress record, automatically updated.
 */
@Entity
@Table(name = "user_goal_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGoalProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private FishingGoal goal;

    @Column(name = "current_value", nullable = false)
    private BigDecimal currentValue;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
