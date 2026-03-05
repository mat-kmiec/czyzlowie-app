package pl.czyzlowie.modules.mail;

public interface EmailSender {
    void sendEmail(String to, String subject, String htmlContent);
}
