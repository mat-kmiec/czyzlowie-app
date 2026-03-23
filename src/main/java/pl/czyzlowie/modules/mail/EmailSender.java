package pl.czyzlowie.modules.mail;

/**
 * An interface that defines the contract for sending emails.
 * Implementations of this interface are responsible for constructing
 * and sending email messages.
 */
public interface EmailSender {
    void sendEmail(String to, String subject, String htmlContent);
}
