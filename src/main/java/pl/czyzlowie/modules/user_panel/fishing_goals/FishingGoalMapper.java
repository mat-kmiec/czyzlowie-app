package pl.czyzlowie.modules.user_panel.fishing_goals;

import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.user_panel.fishing_goals.FishingGoal;
import pl.czyzlowie.modules.user_panel.fishing_goals.GoalProgressViewDto;
import pl.czyzlowie.modules.user_panel.fishing_goals.UserGoalProgress;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class FishingGoalMapper {

    public GoalProgressViewDto toDto(FishingGoal goal, UserGoalProgress progress) {
        BigDecimal current = progress != null ? progress.getCurrentValue() : BigDecimal.ZERO;
        boolean completed = progress != null && progress.isCompleted();

        int percent = 0;
        if (goal.getTargetValue().compareTo(BigDecimal.ZERO) > 0) {
            percent = current.divide(goal.getTargetValue(), 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .intValue();
        }
        percent = Math.min(percent, 100); // Zabezpieczenie przed przekroczeniem 100%

        return GoalProgressViewDto.builder()
                .goalId(goal.getId())
                .title(goal.getTitle())
                .type(goal.getGoalType())
                .visibility(goal.getVisibility())
                .targetValue(goal.getTargetValue())
                .currentValue(current)
                .unit(goal.getUnit())
                .deadline(goal.getDeadline())
                .isCompleted(completed)
                .completedAt(progress != null ? progress.getCompletedAt() : null)
                .completionPercentage(percent)
                .build();
    }
}