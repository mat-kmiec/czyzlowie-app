package pl.czyzlowie.modules.user_panel.favorite_spots;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/**
 * Controller responsible for handling favorite spot management endpoints.
 * This includes viewing the favorite spots, adding new spots, and deleting existing spots.
 */
@Controller
@RequestMapping("/ulubione-miejscowki")
@RequiredArgsConstructor
public class FavoriteSpotController {

    private final FavoriteSpotService favoriteSpotService;

    /**
     * Handles the request to display the user's favorite spots page, populating the model with required data.
     *
     * @param page the current page number for pagination, defaults to 0 if not provided
     * @param size the number of spots to display per page, defaults to 6 if not provided
     * @param model the model object used to pass attributes to the view
     * @param principal the principal object representing the currently authenticated user
     * @return the name of the view to render the favorite spots page
     */
    @GetMapping
    public String showSpotsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            Model model,
            Principal principal) {

        String email = principal.getName();
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);

        model.addAttribute("spotPage", favoriteSpotService.getUserSpots(email, pageable));
        model.addAttribute("totalSpots", favoriteSpotService.getTotalSpots(email));
        model.addAttribute("favoriteWaterType", favoriteSpotService.getFavoriteWaterType(email));
        model.addAttribute("mainTarget", favoriteSpotService.getMainTarget(email));

        if (!model.containsAttribute("spotRequest")) {
            model.addAttribute("spotRequest", new FavoriteSpotRequest());
        }

        return "profil/favorite-places";
    }

    /**
     * Adds a favorite spot to the user's collection.
     *
     * @param request the request object containing the details of the spot to be added
     * @param bindingResult the result of binding the request object during validation
     * @param principal the current authenticated user principal
     * @param redirectAttributes attributes used to pass flash data to the redirected view
     * @return a redirect URL to the user's favorite spots page
     */
    @PostMapping("/dodaj")
    public String addSpot(@Valid @ModelAttribute("spotRequest") FavoriteSpotRequest request,
                          BindingResult bindingResult,
                          Principal principal,
                          RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.spotRequest", bindingResult);
            redirectAttributes.addFlashAttribute("spotRequest", request);
            redirectAttributes.addFlashAttribute("error", "Błąd walidacji formularza. Sprawdź wpisane dane.");
            return "redirect:/ulubione-miejscowki";
        }

        try {
            favoriteSpotService.saveSpot(request, principal.getName());
            redirectAttributes.addFlashAttribute("success", "Pomyślnie dodano tajną lokację!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Wystąpił błąd podczas zapisu.");
        }

        return "redirect:/ulubione-miejscowki";
    }

    /**
     * Deletes a favorite spot associated with the given ID for the currently logged-in user.
     *
     * @param id the ID of the spot to be deleted
     * @param principal the current logged-in user's principal information
     * @param redirectAttributes a container for flash attributes used to pass messages between requests
     * @return a redirect URL to the favorite spots page
     */
    @PostMapping("/{id}/usun")
    public String deleteSpot(@PathVariable Long id,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        try {
            favoriteSpotService.deleteSpot(id, principal.getName());
            redirectAttributes.addFlashAttribute("success", "Lokacja została pomyślnie usunięta.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Nie udało się usunąć lokacji.");
        }
        return "redirect:/ulubione-miejscowki";
    }
}
