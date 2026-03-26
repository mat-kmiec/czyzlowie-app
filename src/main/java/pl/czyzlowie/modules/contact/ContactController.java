package pl.czyzlowie.modules.contact;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/kontakt")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @GetMapping
    public String showContactPage() {
        return "info/contact";
    }

    // Obsługa wysłania formularza
    @PostMapping
    public String handleContactForm(@Valid @ModelAttribute ContactRequest request,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Proszę podać poprawny e-mail i treść wiadomości.");
            return "redirect:/kontakt";
        }

        try {
            contactService.processAndSendContactEmail(request);
            // Komunikat sukcesu, który odbierze Thymeleaf na froncie
            redirectAttributes.addFlashAttribute("successMessage", "Twoja wiadomość została pomyślnie wysłana!");
        } catch (Exception e) {
            // Komunikat błędu
            redirectAttributes.addFlashAttribute("errorMessage", "Wystąpił problem z wysłaniem wiadomości. Spróbuj ponownie później.");
        }

        return "redirect:/kontakt";
    }
}
