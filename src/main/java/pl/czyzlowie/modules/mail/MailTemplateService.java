package pl.czyzlowie.modules.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class MailTemplateService {

    private final EmailSender emailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.url}")
    private String appUrl;

    public void sendBrandedEmail(String to, String subject, String htmlMessage) {
        Context context = new Context();
        context.setVariable("messageContent", htmlMessage);
        context.setVariable("appUrl", appUrl);
        String processedHtml = templateEngine.process("mail/mail-template", context);
        emailSender.sendEmail(to, subject, processedHtml);
    }
}
