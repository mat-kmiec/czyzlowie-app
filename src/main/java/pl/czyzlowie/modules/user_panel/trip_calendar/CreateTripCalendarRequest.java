package pl.czyzlowie.modules.user_panel.trip_calendar;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for creating a new trip calendar entry.
 */
@Data
public class CreateTripCalendarRequest {

    @NotBlank(message = "Nazwa misji jest wymagana")
    private String name;

    @NotBlank(message = "Lokalizacja jest wymagana")
    private String location;

    private BigDecimal latitude;

    private BigDecimal longitude;

    @NotNull(message = "Data rozpoczęcia jest wymagana")
    private LocalDateTime startDate;

    @NotNull(message = "Data zakończenia jest wymagana")
    private LocalDateTime endDate;

    @NotNull(message = "Metoda łowienia jest wymagana")
    private FishingMethod method;

    private String team;

    private String notes;
}
