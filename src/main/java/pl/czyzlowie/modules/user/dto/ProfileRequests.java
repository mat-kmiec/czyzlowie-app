package pl.czyzlowie.modules.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProfileRequests {

    public record ChangeUsernameRequest(
            @NotBlank(message = "Nick nie może być pusty")
            @Size(min = 3, max = 50, message = "Nick musi mieć od 3 do 50 znaków")
            String username
    ) {}

    public record ChangeEmailRequest(
            @NotBlank(message = "E-mail nie może być pusty")
            @Email(message = "Niepoprawny format adresu e-mail")
            String newEmail
    ) {}

    public record ChangePasswordRequest(
            @NotBlank(message = "Aktualne hasło jest wymagane")
            String oldPassword,

            @NotBlank(message = "Nowe hasło jest wymagane")
            @Size(min = 8, message = "Hasło musi mieć minimum 8 znaków")
            String newPassword,

            @NotBlank(message = "Potwierdzenie hasła jest wymagane")
            String confirmPassword
    ) {}
}