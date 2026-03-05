package pl.czyzlowie.modules.auth;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterDto {

    @NotBlank(message = "Email jest wymagany")
    @Email(message = "Podaj poprawny adres email")
    private String email;

    @NotBlank(message = "Nazwa użytkownika jest wymagana")
    @Size(min = 3, max = 20, message = "Nazwa użytkownika musi mieć od 3 do 20 znaków")
    private String username;

    @NotBlank(message = "Hasło jest wymagane")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d\\w\\W]{8,}$",
            message = "Hasło musi mieć min. 8 znaków, wielką literę, małą literę i cyfrę")
    private String password;

    @AssertTrue(message = "Musisz zaakceptować regulamin")
    private boolean termsAccepted;
}
