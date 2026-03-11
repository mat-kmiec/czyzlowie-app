package pl.czyzlowie.modules.user_panel.fishing_trips;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Represents the data required to create a new fishing trip.
 * This class serves as a request model for operations that create fishing trip records.
 * It provides the necessary fields for specifying details such as the trip's name, location,
 * start and end dates, fishing type, rating, tags, and additional notes.
 *
 * Validation constraints ensure that mandatory fields are provided and adhere to specific formats or limits.
 *
 * Components:
 * - tripName: Specifies the name of the fishing trip. Mandatory and limited to 255 characters.
 * - location: Specifies the trip location. Mandatory field.
 * - lat: The latitude coordinates of the fishing location.
 * - lng: The longitude coordinates of the fishing location.
 * - startDate: Specifies the start date and time of the trip. Mandatory field.
 * - endDate: Specifies the end date and time of the trip. Mandatory field.
 * - caughtFish: Indicates the count of fish caught during the trip. Mandatory and cannot be negative.
 * - type: Specifies the fishing method used (e.g., SPINNING, KARPIOWANIE). Mandatory field.
 * - rating: Represents the user's rating for the trip experience. Mandatory field.
 * - tags: A collection of tags describing features of the trip (e.g., BOAT, NIGHT).
 * - note: An optional note or description. Limited to 1000 characters.
 *
 * Utility Method:
 * - isDateValid: Validates the consistency of trip start and end dates, ensuring the end date
 *   is not earlier than the start date.
 */
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