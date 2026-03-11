package pl.czyzlowie.modules.user_panel.fishing_trips;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/dziennik-wypraw")
@RequiredArgsConstructor
public class FishingTripController {

    private final FishingTripService tripService;

    /**
     * Handles the request to display the fishing expedition journal for the authenticated user.
     * It populates the model with the user's fishing trips and statistics related to their fishing activities.
     *
     * @param page the page number to retrieve, default is 0
     * @param size the number of items per page, default is 10
     * @param principal the security principal representing the currently authenticated user
     * @param model the model object used to add attributes to the view
     * @return the name of the view template to render, in this case "profil/expedition-journal"
     */
    @GetMapping
    public String showJournal(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal,
            Model model) {

        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);

        Page<FishingTrip> tripPage = tripService.getUserTrips(principal.getName(), pageable);
        model.addAttribute("totalFish", tripService.getTotalCaughtFish(principal.getName()));
        model.addAttribute("favoriteMethod", tripService.getFavoriteFishingMethod(principal.getName()));

        model.addAttribute("tripPage", tripPage);
        return "profil/expedition-journal";
    }

    /**
     * Handles a POST request to log a fishing trip. Validates the provided trip details,
     * creates the trip, and sets appropriate flash attributes based on the outcome.
     *
     * @param request the CreateFishingTripRequest object containing trip details
     * @param bindingResult validation results for the request object
     * @param principal the currently authenticated principal
     * @param redirectAttributes attributes used to pass feedback messages to the redirected page
     * @return a redirect URL to the fishing trip log page
     */
    @PostMapping
    public String logTrip(@Valid @ModelAttribute CreateFishingTripRequest request,
                          BindingResult bindingResult,
                          Principal principal,
                          RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/dziennik-wypraw";
        }

        try {
            tripService.createTrip(request, principal.getName());
            redirectAttributes.addFlashAttribute("success", "Misja zarchiwizowana pomyślnie!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Wystąpił nieoczekiwany błąd serwera.");
        }

        return "redirect:/dziennik-wypraw";
    }

    /**
     * Deletes a trip identified by its ID and associated with the authenticated user.
     *
     * @param id the ID of the trip to be deleted
     * @param principal the authenticated user performing the deletion
     * @param redirectAttributes attributes used to pass flash messages to the redirected view
     * @return a redirect URL to the trip journal page
     */
    @PostMapping("/{id}/usun")
    public String deleteTrip(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            tripService.deleteTrip(id, principal.getName());
            redirectAttributes.addFlashAttribute("success", "Wyprawa została usunięta.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dziennik-wypraw";
    }
}