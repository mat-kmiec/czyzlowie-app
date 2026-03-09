package pl.czyzlowie.modules.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.mail.MailTemplateService;
import pl.czyzlowie.modules.user.entity.User;
import pl.czyzlowie.modules.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailTemplateService mailTemplateService;

    @Transactional
    public void changeUsername(String currentEmail, String newUsername) {
        User user = getUserByEmail(currentEmail);

        if (user.getUsername().equalsIgnoreCase(newUsername)) {
            return;
        }
        if (userRepository.existsByUsername(newUsername)) {
            throw new IllegalArgumentException("Ten nick jest już zajęty.");
        }

        user.setUsername(newUsername);
        userRepository.save(user);
    }

    @Transactional
    public void changeEmail(String currentEmail, String newEmail) {
        User user = getUserByEmail(currentEmail);

        if (user.getEmail().equalsIgnoreCase(newEmail)) {
            return;
        }
        if (userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Ten adres e-mail jest już zajęty.");
        }

        user.setEmail(newEmail);
        userRepository.save(user);

        String oldEmailMessage = String.format(
                "<p>Cześć <strong>%s</strong>,</p>" +
                        "<p>Informujemy, że adres e-mail powiązany z Twoim kontem na CzyZlowie.pl został przed chwilą zmieniony na: <strong>%s</strong>.</p>" +
                        "<p style=\"color: #e74c3c;\">Jeśli to nie Ty dokonałeś tej zmiany, skontaktuj się z nami natychmiast!</p>",
                user.getUsername(), newEmail
        );
        mailTemplateService.sendBrandedEmail(currentEmail, "Ważne: Zmiana adresu e-mail - CzyZlowie.pl", oldEmailMessage);

        String newEmailMessage = String.format(
                "<p>Cześć <strong>%s</strong>,</p>" +
                        "<p>Twój adres e-mail na platformie CzyZlowie.pl został zaktualizowany. Od teraz to na ten adres będą trafiać nasze wiadomości.</p>",
                user.getUsername()
        );
        mailTemplateService.sendBrandedEmail(newEmail, "Witamy na nowym adresie e-mail - CzyZlowie.pl", newEmailMessage);

    }

    @Transactional
    public void changePassword(String currentEmail, String oldPassword, String newPassword, String confirmPassword) {
        User user = getUserByEmail(currentEmail);

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Aktualne hasło jest nieprawidłowe.");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Nowe hasła nie są identyczne.");
        }
        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Nowe hasło nie może być takie samo jak stare.");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        String passwordChangeMessage = String.format(
                "<p>Cześć <strong>%s</strong>,</p>" +
                        "<p>Twoje hasło do konta na CzyZlowie.pl zostało pomyślnie zmienione.</p>" +
                        "<p>Pamiętaj, aby nie udostępniać swojego hasła nikomu. Jeśli to nie Ty zlecałeś zmianę hasła, <a href=\"#\">zresetuj je natychmiast</a> i skontaktuj się z pomocą techniczną.</p>",
                user.getUsername()
        );
        mailTemplateService.sendBrandedEmail(currentEmail, "Ostrzeżenie bezpieczeństwa: Zmieniono hasło - CzyZlowie.pl", passwordChangeMessage);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Nie znaleziono użytkownika"));
    }
}
