package pl.czyzlowie.modules.user_panel.trip_calendar;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for trip calendar response.
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
