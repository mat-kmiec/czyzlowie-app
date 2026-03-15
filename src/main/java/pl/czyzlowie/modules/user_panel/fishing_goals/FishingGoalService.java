package pl.czyzlowie.modules.user_panel.fishing_goals;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.user.entity.User;
import pl.czyzlowie.modules.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Service class responsible for handling fishing goal operations. It provides methods to manage goals
 * including creation, progress updating, retrieval of active and completed goals, and deletion or resetting progress.
 * The service operates within a transactional context and ensures necessary validations for user actions on goals.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FishingGoalService {

    private final FishingGoalRepository goalRepository;
    private final UserGoalProgressRepository progressRepository;
    private final FishingGoalMapper mapper;

    private final UserRepository userRepository;

    private Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika powiązanego z adresem: " + email));
    }

    /**
     * Retrieves a paginated list of active goals for a user identified by their email.
     *
     * @param email the email address of the user whose active goals are to be retrieved
     * @param pageable the pagination and sorting information
     * @return a page of GoalProgressViewDto objects representing the user's active goals
     */
    @Transactional(readOnly = true)
    public Page<GoalProgressViewDto> getActiveGoals(String email, org.springframework.data.domain.Pageable pageable) {
        Long userId = getUserIdByEmail(email);
        Page<FishingGoal> goalsPage = goalRepository.findActiveGoalsForUser(userId, pageable);
        return mapGoalsToDtoPage(goalsPage, userId);
    }

    /**
     * Retrieves a paginated list of completed goals for a user identified by their email.
     *
     * @param email the email address of the user whose completed goals are to be retrieved
     * @param pageable the pagination information
     * @return a paginated list of {@code GoalProgressViewDto} representing the completed goals
     */
    @Transactional(readOnly = true)
    public Page<GoalProgressViewDto> getCompletedGoals(String email, org.springframework.data.domain.Pageable pageable) {
        Long userId = getUserIdByEmail(email);
        Page<FishingGoal> goalsPage = goalRepository.findCompletedGoalsForUser(userId, pageable);
        return mapGoalsToDtoPage(goalsPage, userId);
    }

    /**
     * Maps a page of FishingGoal entities to a page of GoalProgressViewDto objects.
     * The mapping includes user-specific progress data if available from the UserGoalProgress repository.
     *
     * @param goalsPage the page of FishingGoal entities to be mapped
     * @param userId the ID of the user whose progress data should be included in the mapping
     * @return a page of GoalProgressViewDto objects, with progress data included if available
     */
    private Page<GoalProgressViewDto> mapGoalsToDtoPage(Page<FishingGoal> goalsPage, Long userId) {
        List<Long> goalIds = goalsPage.getContent().stream()
                .map(FishingGoal::getId)
                .toList();

        if (goalIds.isEmpty()) {
            return goalsPage.map(goal -> mapper.toDto(goal, null));
        }

        Map<Long, UserGoalProgress> progressMap = progressRepository.findAllByUserIdAndGoalIdIn(userId, goalIds).stream()
                .collect(Collectors.toMap(p -> p.getGoal().getId(), p -> p));

        return goalsPage.map(goal -> mapper.toDto(goal, progressMap.get(goal.getId())));
    }

    /**
     * Creates a new fishing goal based on the provided details. The goal can either be private or global,
     * contingent upon the user's administrative status and the specified parameters.
     *
     * @param email the email address of the user creating the goal; used to retrieve the associated user ID
     * @param dto the data transfer object containing details about the fishing goal to be created
     * @param isAdmin a boolean flag indicating whether the user creating the goal has administrative privileges
     */
    @Transactional
    public void createGoal(String email, CreateFishingGoalDto dto, boolean isAdmin) {
        Long userId = getUserIdByEmail(email);
        GoalVisibility visibility = (isAdmin && dto.isGlobal()) ? GoalVisibility.GLOBAL : GoalVisibility.PRIVATE;

        FishingGoal goal = FishingGoal.builder()
                .title(dto.getTitle())
                .goalType(dto.getType())
                .visibility(visibility)
                .targetValue(dto.getTargetValue())
                .unit(dto.getUnit())
                .deadline(dto.getDeadline())
                .creatorId(visibility == GoalVisibility.PRIVATE ? userId : null)
                .build();

        goal = goalRepository.save(goal);

        if (visibility == GoalVisibility.PRIVATE) {
            UserGoalProgress progress = UserGoalProgress.builder()
                    .userId(userId)
                    .goal(goal)
                    .currentValue(BigDecimal.ZERO)
                    .isCompleted(false)
                    .build();
            progressRepository.save(progress);
        }
    }

    /**
     * Updates the progress of a user's goal with a new value. If the new value meets or exceeds the goal's target value,
     * it marks the goal as completed.
     *
     * @param email   the email of the user whose goal progress is being updated
     * @param goalId  the identifier of the goal to update
     * @param newValue the new progress value to set
     * @return true if the goal is completed as a result of the update, false otherwise
     * @throws IllegalArgumentException if the goal does not exist, the new value is less than the current value,
     *                                  or any other validation rules are violated
     */
    @Transactional
    public boolean updateProgress(String email, Long goalId, BigDecimal newValue) {
        Long userId = getUserIdByEmail(email);

        FishingGoal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Cel nie istnieje"));

        UserGoalProgress progress = progressRepository.findByUserIdAndGoalId(userId, goalId)
                .orElseGet(() -> UserGoalProgress.builder()
                        .userId(userId)
                        .goal(goal)
                        .currentValue(BigDecimal.ZERO)
                        .isCompleted(false)
                        .build());

        if (newValue.compareTo(progress.getCurrentValue()) < 0) {
            throw new IllegalArgumentException("Nowa wartość nie może być mniejsza od obecnej.");
        }

        progress.setCurrentValue(newValue);
        progress.setUpdatedAt(LocalDateTime.now());

        boolean justCompleted = false;

        if (newValue.compareTo(goal.getTargetValue()) >= 0 && !progress.isCompleted()) {
            progress.setCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
            justCompleted = true;
            log.info("Użytkownik (ID: {}) zrealizował kontrakt: {}", userId, goal.getTitle());
        }

        progressRepository.save(progress);

        return justCompleted;
    }

    /**
     * Deletes a fishing goal identified by its ID. The operation differs based on goal visibility:
     * if the goal is private and owned by the user, it is completely deleted;
     * if the goal is global, only the associated progress is cleared.
     *
     * @param goalId the identifier of the fishing goal to be deleted
     * @param email the email of the user attempting to delete the goal
     * @throws IllegalArgumentException if the goal with the given ID does not exist
     * @throws IllegalStateException if the user does not have permission to delete the goal
     */
    @Transactional
    public void deleteGoal(Long goalId, String email) {
        Long userId = getUserIdByEmail(email);

        FishingGoal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Cel nie istnieje"));

        if (goal.getVisibility() == GoalVisibility.PRIVATE && userId.equals(goal.getCreatorId())) {

            progressRepository.findByUserIdAndGoalId(userId, goalId)
                    .ifPresent(progressRepository::delete);

            goalRepository.delete(goal);
            log.info("Użytkownik (ID: {}) usunął kontrakt: {}", userId, goal.getTitle());

        } else if (goal.getVisibility() == GoalVisibility.GLOBAL) {
            progressRepository.findByUserIdAndGoalId(userId, goalId)
                    .ifPresent(progressRepository::delete);
            log.info("Użytkownik (ID: {}) wyzerował postęp w globalnym kontrakcie: {}", userId, goal.getTitle());
        } else {
            throw new IllegalStateException("Brak uprawnień do usunięcia tego kontraktu.");
        }
    }

    /**
     * Resets the progress of a specific goal for a user identified by their email.
     * The method sets the current value of the progress to zero, marks it as not completed,
     * and removes the completion timestamp.
     *
     * @param goalId the ID of the goal whose progress is to be reset
     * @param email the email of the user for whom the progress needs to be reset
     */
    @Transactional
    public void resetProgress(Long goalId, String email) {
        Long userId = getUserIdByEmail(email);

        progressRepository.findByUserIdAndGoalId(userId, goalId)
                .ifPresent(progress -> {
                    progress.setCurrentValue(BigDecimal.ZERO);
                    progress.setCompleted(false);
                    progress.setCompletedAt(null);
                    progressRepository.save(progress);
                    log.info("Użytkownik (ID: {}) zresetował postęp w celu: {}", userId, progress.getGoal().getTitle());
                });
    }
}