package pl.czyzlowie.modules.user_panel.fishing_goals;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing the progress view of a specific fishing goal.
 *
 * This class is used to provide summarized information about a user's fishing goal
 * and its associated progress status. It includes details such as the goal's attributes,
 * progress metrics, and completion status, simplifying data sharing between the application
 * layers (e.g., service, controller layers).
 *
 * Fields:
 * - goalId: Unique identifier of the fishing goal.
 * - title: Name or title of the fishing goal.
 * - type: The type of the fishing goal (e.g., LENGTH, WEIGHT, QUANTITY, TRIPS).
 * - visibility: The visibility setting of the fishing goal (e.g., PRIVATE, GLOBAL).
 * - targetValue: The target value the user aims to achieve for this goal.
 * - currentValue: The current progress value of the fishing goal.
 * - unit: Unit of measurement for the goal's target value.
 * - deadline: The optional deadline for achieving the goal.
 * - isCompleted: Flag indicating whether the goal has been completed.
 * - completedAt: Timestamp noting when the goal was completed, if applicable.
 * - completionPercentage: Percentage of the goal's completion based on current and target values.
 */
@Data
@Builder
public class GoalProgressViewDto {
    private Long goalId;
    private String title;
    private GoalType type;
    private GoalVisibility visibility;
    private BigDecimal targetValue;
    private BigDecimal currentValue;
    private String unit;
    private LocalDate deadline;
    private boolean isCompleted;
    private LocalDateTime completedAt;
    private int completionPercentage;
}
