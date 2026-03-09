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