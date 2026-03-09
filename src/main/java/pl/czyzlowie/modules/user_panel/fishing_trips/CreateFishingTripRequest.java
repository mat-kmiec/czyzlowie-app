package pl.czyzlowie.modules.user_panel.fishing_trips;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record CreateFishingTripRequest(
        @NotBlank(message = "Nazwa wyprawy nie może być pusta")
        @Size(max = 255, message = "Nazwa jest za długa")
        String tripName,

        @NotBlank(message = "Lokalizacja jest wymagana")
        String location,

        BigDecimal lat,
        BigDecimal lng,

        @NotNull(message = "Data rozpoczęcia jest wymagana")
        LocalDateTime startDate,

        @NotNull(message = "Data zakończenia jest wymagana")
        LocalDateTime endDate,

        @NotNull(message = "Podaj ilość złowionych ryb (wpisz 0 jeśli był blank)")
        @Min(value = 0, message = "Ilość ryb nie może być ujemna")
        Integer caughtFish,

        @NotBlank(message = "Wybierz metodę")
        String type,

        @NotBlank(message = "Oceń wyprawę")
        String rating,

        Set<String> tags,

        @Size(max = 1000, message = "Notatka może mieć max 1000 znaków")
        String note
) {
    public boolean isDateValid() {
        return startDate != null && endDate != null && !endDate.isBefore(startDate);
    }
}