package pl.czyzlowie.modules.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.mail.MailTemplateService;
import pl.czyzlowie.modules.user.entity.User;
import pl.czyzlowie.modules.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final MailTemplateService mailTemplateService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.url:http://localhost:8080}")
    private String appUrl;

    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d\\w\\W]{8,}$";

    @Transactional
    public boolean createAndSendPasswordResetToken(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();

        tokenRepository.deleteByUser(user);

        String tokenStr = UUID.randomUUID().toString();

        PasswordResetToken token = PasswordResetToken.builder()
                .token(tokenStr)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();

        tokenRepository.save(token);

        String resetLink = appUrl + "/reset-hasla?token=" + tokenStr;
        String htmlMessage = "<h2 style='color: #1e293b; margin-top: 0;'>Witaj " + user.getUsername() + "!</h2>" +
                "<p style='color: #334155; font-size: 16px; line-height: 1.6;'>Otrzymaliśmy prośbę o zresetowanie hasła do Twojego konta na platformie CzyZlowie.pl.</p>" +
                "<p style='text-align: center; margin: 30px 0;'>" +
                "<a href='" + resetLink + "' style='background-color: #ef4444; color: #ffffff; text-decoration: none; padding: 12px 25px; border-radius: 5px; font-weight: bold; display: inline-block;'>Zresetuj hasło</a>" +
                "</p>" +
                "<p style='color: #64748b; font-size: 14px;'>Jeśli to nie Ty prosiłeś o reset, zignoruj tę wiadomość. Twój link wygaśnie za godzinę.</p>";

        mailTemplateService.sendBrandedEmail(user.getEmail(), "Reset hasła - CzyZlowie.pl", htmlMessage);
        return true;
    }

    public boolean isTokenValid(String token) {
        return tokenRepository.findByToken(token)
                .map(t -> !t.isExpired())
                .orElse(false);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        // ZABEZPIECZENIE 2: Twarda walidacja hasła na backendzie
        if (newPassword == null || !Pattern.matches(PASSWORD_PATTERN, newPassword)) {
            throw new IllegalArgumentException("Hasło musi mieć min. 8 znaków, wielką literę, małą literę i cyfrę.");
        }

        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Nieprawidłowy token."));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new IllegalArgumentException("Twój link wygasł. Wygeneruj nowy.");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(resetToken);
    }
}