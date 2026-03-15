package pl.czyzlowie.modules.user_panel.fishing_goals;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;

/**
 * The FishingGoalController handles HTTP requests related to managing fishing goals.
 * It provides endpoints for creating, updating, resetting, deleting, and viewing fishing goals.
 * The goal management includes separating active and completed goals, as well as handling user interactions
 * with fishing goals through forms and redirects.
 */
@Controller
@RequestMapping("/cele")
@RequiredArgsConstructor
public class FishingGoalController {

    private final FishingGoalService fishingGoalService;

    @ModelAttribute("goalRequest")
    public CreateFishingGoalDto goalRequest() {
        return new CreateFishingGoalDto();
    }

    /**
     * Populates the dashboard model with active and completed goals.
     *
     * @param model the model to which attributes are added
     * @param email the email of the user whose goals are being retrieved
     * @param activePage the page number of active goals to fetch
     * @param completedPage the page number of completed goals to fetch
     * @param size the number of items per page
     */
    private void populateDashboardModel(Model model, String email, int activePage, int completedPage, int size) {
        Page<GoalProgressViewDto> activeGoalsPage = fishingGoalService.getActiveGoals(
                email, PageRequest.of(activePage, size)
        );
        Page<GoalProgressViewDto> completedGoalsPage = fishingGoalService.getCompletedGoals(
                email, PageRequest.of(completedPage, size)
        );

        model.addAttribute("activeGoalsPage", activeGoalsPage);
        model.addAttribute("completedGoalsPage", completedGoalsPage);
        model.addAttribute("activeCount", activeGoalsPage.getTotalElements());
        model.addAttribute("completedCount", completedGoalsPage.getTotalElements());
    }

    /**
     * Handles GET requests to display the fishing goals board.
     *
     * @param activePage the current active page number; defaults to 0 if not provided
     * @param completedPage the current completed page number; defaults to 0 if not provided
     * @param size the size of the pages to be displayed; defaults to 4 if not provided
     * @param model the model object used to pass attributes to the view
     * @param principal the authenticated user principal
     * @return the name of the view to be rendered, in this case "profil/fishing-goals"
     */
    @GetMapping
    public String showGoalsBoard(
            @RequestParam(defaultValue = "0") int activePage,
            @RequestParam(defaultValue = "0") int completedPage,
            @RequestParam(defaultValue = "4") int size,
            Model model, Principal principal) {

        populateDashboardModel(model, principal.getName(), activePage, completedPage, size);

        return "profil/fishing-goals";
    }

    /**
     * Handles the creation of a new fishing goal and manages the appropriate response based on input validation and service execution.
     *
     * @param goalRequest the data transfer object containing the details of the new fishing goal
     * @param bindingResult the result of validating the provided goalRequest object
     * @param activePage the current page index for active goals in the user's dashboard
     * @param completedPage the current page index for completed goals in the user's dashboard
     * @param size the number of items displayed per page
     * @param principal the currently authenticated user's security context providing username information
     * @param model the model object used to populate data for the corresponding view
     * @param redirectAttributes attributes used for temporary storage of messages to be shown on a redirected page
     * @return a string representing the name of the view to be rendered or a redirect URL based on the request handling result
     */
    @PostMapping("/dodaj")
    public String createGoal(@Valid @ModelAttribute("goalRequest") CreateFishingGoalDto goalRequest,
                             BindingResult bindingResult,
                             @RequestParam(defaultValue = "0") int activePage,
                             @RequestParam(defaultValue = "0") int completedPage,
                             @RequestParam(defaultValue = "4") int size,
                             Principal principal,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            populateDashboardModel(model, principal.getName(), activePage, completedPage, size);
            model.addAttribute("error", "Formularz zawiera błędy. Sprawdź wpisane dane.");
            return "profil/fishing-goals";
        }

        try {
            boolean isAdmin = false;
            fishingGoalService.createGoal(principal.getName(), goalRequest, isAdmin);
            redirectAttributes.addFlashAttribute("success", "Pomyślnie wystawiono nowy kontrakt!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Wystąpił błąd podczas zapisu celu.");
        }

        return "redirect:/cele";
    }

    /**
     * Updates the progress of a specific goal for the currently authenticated user.
     *
     * @param goalId the ID of the goal to update
     * @param newProgress the new progress value to be set for the goal
     * @param principal the security context containing the authenticated user's information
     * @param redirectAttributes attributes used to pass messages back to the redirected page
     * @return the URL to redirect to after completing the update process
     */
    @PostMapping("/aktualizuj")
    public String updateProgress(@RequestParam Long goalId,
                                 @RequestParam BigDecimal newProgress,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        try {
            boolean justCompleted = fishingGoalService.updateProgress(principal.getName(), goalId, newProgress);
            if (justCompleted) {
                redirectAttributes.addFlashAttribute("success", "Cel osiągnięty! Gratulacje!");
            } else {
                redirectAttributes.addFlashAttribute("success", "Postęp został zaktualizowany.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Błąd aktualizacji: " + e.getMessage());
        }
        return "redirect:/cele";
    }

    /**
     * Resets the progress of a specific goal associated with the given ID for the currently authenticated user.
     *
     * @param id the unique identifier of the goal whose progress is being reset
     * @param principal the security principal representing the currently authenticated user
     * @param redirectAttributes holder for model attributes that should be stored in the session and accessed after a redirect
     * @return a redirect URL to the goals overview page after attempting to reset the goal progress
     */
    @PostMapping("/{id}/reset")
    public String resetGoalProgress(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            fishingGoalService.resetProgress(id, principal.getName());
            redirectAttributes.addFlashAttribute("success", "Postęp został wyzerowany. Zaczynamy od nowa!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Nie udało się zresetować postępu.");
        }
        return "redirect:/cele";
    }

    /**
     * Deletes the goal identified by the given ID for the authenticated user.
     * Redirects to the goals page with a success or error message based on the operation result.
     *
     * @param id the ID of the goal to be deleted
     * @param principal the currently authenticated user's security principal
     * @param redirectAttributes attributes for a redirect scenario, to pass success or error messages
     * @return a redirection string to the goals page
     */
    @PostMapping("/{id}/usun")
    public String deleteGoal(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            fishingGoalService.deleteGoal(id, principal.getName());
            redirectAttributes.addFlashAttribute("success", "Kontrakt został porzucony.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Nie udało się usunąć kontraktu.");
        }
        return "redirect:/cele";
    }
}