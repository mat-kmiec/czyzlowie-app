package pl.czyzlowie.modules.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Service responsible for sending branded HTML emails using a predefined template.
 * This service integrates with an email-sending utility and a template engine to
 * compose and send custom emails to recipients. It processes the HTML template by
 * injecting variables such as the message content and application URL.
 */
@Service
@RequiredArgsConstructor
public class MailTemplateService {

    private final EmailSender emailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.url}")
    private String appUrl;

    /**
     * Sends a branded email to a specified recipient with a custom subject and HTML message content.
     * The method processes the HTML template, injecting the provided message content and application URL,
     * and sends the email using the configured email sender.
     *
     * @param to the recipient's email address
     * @param subject the subject of the email
     * @param htmlMessage the HTML content to be included in the email body
     */
    public void sendBrandedEmail(String to, String subject, String htmlMessage) {
        Context context = new Context();
        context.setVariable("messageContent", htmlMessage);
        context.setVariable("appUrl", appUrl);
        String processedHtml = templateEngine.process("mail/mail-template", context);
        emailSender.sendEmail(to, subject, processedHtml);
    }
}
