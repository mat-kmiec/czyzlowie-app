package pl.czyzlowie.modules.user_panel.fishing_goals;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO representing the creation of a fishing goal.
 *
 * This class is used to transfer data related to the creation of a new fishing goal
 * by a user. It encapsulates details such as the goal's title, type, target value,
 * unit of measurement, deadline, and whether the goal is globally visible.
 *
 * Fields:
 * - title: The title or name of the fishing goal, which must not be blank.
 * - type: The type of goal, represented by the GoalType enumeration (e.g., LENGTH, WEIGHT, QUANTITY, TRIPS).
 * - targetValue: The target value to be achieved for the goal, which must be a positive number.
 * - unit: The unit of measurement for the goal's target value (e.g., kg, cm, trips).
 * - deadline: The deadline or end date by which the goal should be achieved. The date must follow the "yyyy-MM-dd" format.
 * - isGlobal: A flag indicating whether the goal is visible globally or privately within the application.
 *
 * Annotations:
 * - Lombok annotations (@Data, @NoArgsConstructor, @AllArgsConstructor, @Builder) are used for boilerplate code reduction.
 * - Validation annotations (@NotBlank, @NotNull, @Positive) ensure data integrity.
 * - @DateTimeFormat is used to enforce the format for the deadline field.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFishingGoalDto {
    @NotBlank
    private String title;

    @NotNull
    private GoalType type;

    @NotNull
    @Positive
    private BigDecimal targetValue;

    @NotBlank
    private String unit;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;

    private boolean isGlobal;
}