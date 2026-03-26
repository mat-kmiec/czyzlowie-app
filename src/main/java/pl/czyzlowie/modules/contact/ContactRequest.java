package pl.czyzlowie.modules.contact;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContactRequest {

    private String name;

    @Email(message = "Podaj poprawny adres e-mail lub zostaw to pole puste.")
    private String email;

    private String subject;

    @NotBlank(message = "Wiadomość nie może być pusta.")
    private String message;

    public String getEmail() {
        return (email != null && email.trim().isEmpty()) ? null : email;
    }
}