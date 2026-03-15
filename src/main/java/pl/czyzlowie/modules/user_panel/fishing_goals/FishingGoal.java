package pl.czyzlowie.modules.user_panel.fishing_goals;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import pl.czyzlowie.modules.user_panel.fishing_goals.GoalType;
import pl.czyzlowie.modules.user_panel.fishing_goals.GoalVisibility;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a specific fishing goal created by a user.
 * This entity is mapped to the "fishing_goals" table in the database
 * and provides the structure for storing and managing user-defined goals
 * related to fishing activities.
 *
 * Fields:
 * - id: Unique identifier for the fishing goal.
 * - title: Title or name of the fishing goal.
 * - goalType: The type of the goal, e.g., LENGTH, WEIGHT, QUANTITY, or TRIPS.
 * - visibility: Visibility of the goal, indicating whether it is PRIVATE or GLOBAL.
 * - targetValue: The target value to accomplish the fishing goal.
 * - unit: Unit of measurement for the target value, such as "kg", "cm", or "trips".
 * - deadline: Optional deadline by which the goal should be achieved.
 * - creatorId: Identifier of the user who created the goal.
 * - createdAt: Timestamp indicating when the goal was created.
 */
@Entity
@Table(name = "fishing_goals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FishingGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal_type", nullable = false)
    private GoalType goalType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalVisibility visibility;

    @Column(name = "target_value", nullable = false)
    private BigDecimal targetValue;

    @Column(nullable = false)
    private String unit;

    private LocalDate deadline;

    @Column(name = "creator_id")
    private Long creatorId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}

