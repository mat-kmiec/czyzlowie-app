package pl.czyzlowie.modules.contact;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.mail.MailTemplateService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactService {

    private final MailTemplateService mailTemplateService;

    @Value("${app.mail.admin:kontakt@czyzlowie.pl}")
    private String adminEmail;

    public void processAndSendContactEmail(ContactRequest request) {
        String subjectLabel = translateSubject(request.getSubject());
        String emailSubject = "🎣 Nowa wiadomość: " + subjectLabel;
        String htmlContent = buildAdminNotificationHtml(request, subjectLabel);

        log.info("Wysyłanie formularza kontaktowego od: {}", request.getEmail());
        mailTemplateService.sendBrandedEmail(adminEmail, emailSubject, htmlContent);
    }

    // Mapowanie wartości ze znaczników <option> na ładne nazwy
    private String translateSubject(String rawSubject) {
        if (rawSubject == null) return "Brak tematu";
        return switch (rawSubject) {
            case "sugestia" -> "Sugestia do aplikacji";
            case "blad" -> "Zgłoszenie błędu";
            case "wspolpraca" -> "Współpraca";
            case "inne" -> "Inne / Chcę pogadać";
            default -> rawSubject;
        };
    }

    // Budujemy środek maila
    private String buildAdminNotificationHtml(ContactRequest req, String subject) {
        String name = (req.getName() != null && !req.getName().isBlank()) ? req.getName() : "Użytkownik (nie podano imienia)";
        String userEmail = req.getEmail();
        String message = req.getMessage();

        return String.format(
                "<div style=\"font-family: Arial, sans-serif; color: #333;\">" +
                        "<h2 style=\"color: #2ecc71;\">Otrzymałeś nową wiadomość!</h2>" +
                        "<p><strong>Od:</strong> %s</p>" +
                        "<p><strong>E-mail zwrotny:</strong> <a href=\"mailto:%s\" style=\"color: #2ecc71;\">%s</a></p>" +
                        "<p><strong>Temat:</strong> %s</p>" +
                        "<hr style=\"border: none; border-top: 1px solid #eee; margin: 20px 0;\"/>" +
                        "<h3 style=\"margin-bottom: 10px;\">Treść wiadomości:</h3>" +
                        "<div style=\"background-color: #f8fafc; padding: 15px; border-radius: 8px; border-left: 4px solid #2ecc71; white-space: pre-wrap; line-height: 1.5;\">" +
                        "%s" +
                        "</div>" +
                        "<p style=\"margin-top: 25px; font-size: 0.9em; color: #777;\">" +
                        "Aby odpowiedzieć na tę wiadomość, po prostu kliknij w adres e-mail nadawcy powyżej." +
                        "</p>" +
                        "</div>",
                name, userEmail, userEmail, subject, message
        );
    }
}
