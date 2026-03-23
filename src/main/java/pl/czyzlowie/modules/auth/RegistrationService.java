package pl.czyzlowie.modules.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.mail.MailTemplateService;
import pl.czyzlowie.modules.user.entity.*;
import pl.czyzlowie.modules.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository tokenRepository;
    private final MailTemplateService mailTemplateService;

    @Value("${app.url:http://localhost:8080}")
    private String appUrl;

    @Transactional
    public void registerUser(RegisterDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Konto z tym adresem email już istnieje.");
        }

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Ten nick jest już zajęty przez innego wędkarza. Wybierz inny!");
        }

        User newUser = User.builder()
                .email(dto.getEmail())
                .username(dto.getUsername())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .provider(AuthProvider.LOCAL)
                .role(Role.USER)
                .isEmailVerified(false)
                .createdAt(LocalDateTime.now())
                .build();

        newUser.setConsents(UserConsents.builder().termsAccepted(dto.isTermsAccepted()).termsVersion("v1.0").build());
        newUser.setStatistics(UserStatistics.builder().build());

        userRepository.save(newUser);

        generateAndSendToken(newUser);
    }

    @Transactional
    public void verifyAccount(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Nieprawidłowy token aktywacyjny."));

        User user = verificationToken.getUser();

        if (verificationToken.isExpired()) {
            tokenRepository.delete(verificationToken);
            generateAndSendToken(user);
            throw new IllegalArgumentException("Twój link aktywacyjny wygasł. Wygenerowaliśmy nowy i wysłaliśmy na Twój adres email. Sprawdź skrzynkę!");
        }

        user.setEmailVerified(true);
        userRepository.save(user);
        tokenRepository.delete(verificationToken);
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Nie mogliśmy pobrać Twojego adresu e-mail. Wpisz go ponownie podczas logowania.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono konta z tym adresem e-mail."));

        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("Twoje konto jest już aktywne! Możesz się zalogować.");
        }
        VerificationToken token = tokenRepository.findByUser(user).orElse(new VerificationToken());
        String newTokenString = UUID.randomUUID().toString();
        token.setUser(user);
        token.setToken(newTokenString);
        token.setExpiryDate(LocalDateTime.now().plusHours(24));
        tokenRepository.save(token);
        sendEmailWithToken(user, newTokenString);
    }

    private void generateAndSendToken(User user) {
        String tokenString = UUID.randomUUID().toString();

        VerificationToken verificationToken = VerificationToken.builder()
                .token(tokenString)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();

        tokenRepository.save(verificationToken);
        sendEmailWithToken(user, tokenString);
    }

    private void sendEmailWithToken(User user, String tokenString) {
        String verificationLink = appUrl + "/weryfikacja?token=" + tokenString;
        String htmlMessage = "<h2 style='color: #1e293b; margin-top: 0;'>Witaj " + user.getUsername() + "!</h2>" +
                "<p style='color: #334155; font-size: 16px; line-height: 1.6;'>Dziękujemy za dołączenie do naszej wędkarskiej społeczności CzyZlowie.pl. " +
                "Zanim wyruszysz na wirtualne łowisko, prosimy o potwierdzenie adresu email.</p>" +
                "<p style='text-align: center; margin: 30px 0;'>" +
                "<a href='" + verificationLink + "' style='background-color: #1e293b; color: #ffffff; text-decoration: none; padding: 12px 25px; border-radius: 5px; font-weight: bold; display: inline-block;'>Aktywuj konto</a>" +
                "</p>" +
                "<p style='color: #64748b; font-size: 14px;'>Jeśli to nie Ty zakładałeś konto, zignoruj tę wiadomość.<br>Połamania kija!</p>";

        mailTemplateService.sendBrandedEmail(user.getEmail(), "Aktywacja konta - CzyZlowie", htmlMessage);
    }
}