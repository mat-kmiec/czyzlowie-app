package pl.czyzlowie.modules.user_panel.trip_calendar;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for the TripCalendar entity.
 * This class is used to transfer data related to fishing trips between different layers of the application.
 *
 * Fields:
 * - id: Unique identifier of the trip.
 * - name: The name of the trip.
 * - location: The location where the trip takes place.
 * - latitude: The latitude coordinate of the trip location.
 * - longitude: The longitude coordinate of the trip location.
 * - startDate: The start date and time of the trip.
 * - endDate: The end date and time of the trip.
 * - method: The fishing method used during the trip, as specified in the FishingMethod enum.
 * - team: Team members involved in the trip, if applicable.
 * - notes: Additional notes or remarks about the trip.
 * - createdAt: The timestamp when this trip entry was created.
 * - updatedAt: The timestamp of the last update to this trip entry.
 */
@Data
public class TripCalendarDto {

    private Long id;
    private String name;
    private String location;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private FishingMethod method;
    private String team;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
