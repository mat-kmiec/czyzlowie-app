package pl.czyzlowie.modules.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService implements EmailSender {

    private final JavaMailSender javaMailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Override
    @Async
    public void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom(new InternetAddress(fromEmail, "CzyZlowie.pl"));
            ClassPathResource logoResource = new ClassPathResource("static/assets/logo.png");
            helper.addInline("logoResource", logoResource);
            javaMailSender.send(mimeMessage);
            log.info("Wysłano email na adres: {}", to);

        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Błąd wysyłki do {}: {}", to, e.getMessage());
            throw new IllegalStateException("Błąd wysyłania emaila", e);
        }
    }
}
