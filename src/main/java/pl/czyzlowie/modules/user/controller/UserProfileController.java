package pl.czyzlowie.modules.user.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.czyzlowie.modules.user.dto.ProfileRequests;
import pl.czyzlowie.modules.user.entity.User;
import pl.czyzlowie.modules.user.service.UserProfileService;

import java.security.Principal;

@Controller
@RequestMapping("/profil")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService profileService;

    @GetMapping
    public String showProfile(Principal principal, Model model) {
        User currentUser = profileService.getUserByEmail(principal.getName());
        model.addAttribute("user", currentUser);
        return "profil/profil";
    }

    @PostMapping("/username")
    public String changeUsername(@Valid @ModelAttribute ProfileRequests.ChangeUsernameRequest request,
                                 BindingResult bindingResult,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/profil";
        }
        try {
            profileService.changeUsername(principal.getName(), request.username());
            redirectAttributes.addFlashAttribute("success", "Pomyślnie zmieniono nick.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profil";
    }

    @PostMapping("/email")
    public String changeEmail(@Valid @ModelAttribute ProfileRequests.ChangeEmailRequest request,
                              BindingResult bindingResult,
                              Principal principal,
                              HttpServletRequest httpRequest,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/profil";
        }
        try {
            profileService.changeEmail(principal.getName(), request.newEmail());
            httpRequest.logout();
            return "redirect:/login?emailChanged=true";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/profil";
        } catch (ServletException e) {
            return "redirect:/login?emailChanged=true";
        }
    }

    @PostMapping("/password")
    public String changePassword(@Valid @ModelAttribute ProfileRequests.ChangePasswordRequest request,
                                 BindingResult bindingResult,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/profil";
        }
        try {
            profileService.changePassword(principal.getName(), request.oldPassword(), request.newPassword(), request.confirmPassword());
            redirectAttributes.addFlashAttribute("success", "Hasło zostało pomyślnie zmienione.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profil";
    }
}
