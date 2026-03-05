package pl.czyzlowie.modules.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final RegistrationService registrationService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }


    @GetMapping("/rejestracja")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "auth/register";
    }

    @PostMapping("/rejestracja")
    public String processRegistration(@Valid @ModelAttribute("registerDto") RegisterDto registerDto,
                                      BindingResult bindingResult,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            registrationService.registerUser(registerDto);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Konto zostało utworzone! Sprawdź swoją skrzynkę email i kliknij w link aktywacyjny, aby móc się zalogować.");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/weryfikacja")
    public String verifyAccount(@RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        try {
            registrationService.verifyAccount(token);
            redirectAttributes.addFlashAttribute("successMessage", "Twoje konto zostało pomyślnie aktywowane! Możesz się teraz zalogować i sprawdzić gdzie biorą ryby.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/login";
    }

    @GetMapping("/resend-activation")
    public String resendActivation(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        try {
            registrationService.resendVerificationEmail(email);
            redirectAttributes.addFlashAttribute("successMessage", "Nowy link aktywacyjny został wysłany! Sprawdź swoją skrzynkę.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/login";
    }
}