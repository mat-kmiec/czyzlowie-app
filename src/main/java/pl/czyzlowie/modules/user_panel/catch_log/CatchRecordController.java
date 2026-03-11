package pl.czyzlowie.modules.user_panel.catch_log;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/**
 * A controller that handles HTTP requests for the user's fishing catch records.
 * It provides functionality such as displaying a user's catch log, saving a new catch,
 * and deleting an existing catch entry. The controller operates under the "/moje-polowy" URL mapping.
 */
@Controller
@RequestMapping("/moje-polowy")
@RequiredArgsConstructor
public class CatchRecordController {

    private final CatchRecordService catchService;

    /**
     * Handles the HTTP GET request to display the user's catch log.
     *
     * @param principal the authenticated user's principal containing user information
     * @param page the requested page index for paginated results, defaults to 0
     * @param size the requested page size for paginated results, defaults to 10
     * @param model the model object to store attributes for rendering the view
     * @return the name of the view template for displaying the user's catch log
     */
    @GetMapping
    public String showCatchLog(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<CatchRecordResponse> catchesPage = catchService.getUserCatches(principal.getName(), pageable);

        model.addAttribute("catchesPage", catchesPage);

        if (!model.containsAttribute("catchRequest")) {
            model.addAttribute("catchRequest", new CatchRecordCreateRequest());
        }

        return "profil/my-catches";
    }

    /**
     * Handles the saving of a catch record by the user.
     * Validates input, processes the request, and provides appropriate success or error messages.
     *
     * @param principal The principal object representing the currently authenticated user.
     * @param request The CatchRecordCreateRequest object containing details of the catch to be saved.
     * @param bindingResult The object used to hold validation errors for the request.
     * @param photo The optional MultipartFile representing the photo of the catch.
     * @param redirectAttributes Used to add attributes to the redirect request for success or error messages.
     * @return A redirect URL to the user's catches page ("/moje-polowy").
     */
    @PostMapping("/zapisz")
    public String saveCatch(
            Principal principal,
            @Valid @ModelAttribute("catchRequest") CatchRecordCreateRequest request,
            BindingResult bindingResult,
            @RequestParam(value = "catchPhoto", required = false) MultipartFile photo,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.catchRequest", bindingResult);
            redirectAttributes.addFlashAttribute("catchRequest", request);
            return "redirect:/moje-polowy";
        }

        try {
            catchService.createCatch(principal.getName(), request, photo);
            redirectAttributes.addFlashAttribute("successMessage", "Zdobycz została pomyślnie zarejestrowana!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Wystąpił błąd podczas zapisu: " + e.getMessage());
        }

        return "redirect:/moje-polowy";
    }

    /**
     * Deletes a catch entry with the specified ID, verifying the user performing the action and handling any access or other errors.
     *
     * @param id the unique identifier of the catch to be deleted
     * @param principal the authenticated user's security*/
    @PostMapping("/{id}/usun")
    public String deleteCatch(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            catchService.deleteCatch(id, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Zdobycz wraz ze zdjęciem została trwale usunięta ze statków bazy.");
        } catch (SecurityException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Wystąpił nieoczekiwany błąd podczas kasowania wpisu.");
        }
        return "redirect:/moje-polowy";
    }
}