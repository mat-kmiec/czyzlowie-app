package pl.czyzlowie.modules.sun.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object representing an individual event in a timeline, typically used to
 * describe solar events or related occurrences in a structured, detailed manner.
 *
 * This class encapsulates details about the event's time range, title, description,
 * and various visual aspects including icon and styling properties for customization.
 * It also indicates whether the event occurs during the golden hour.
 */
@Getter
@Builder
public class TimelineEventDto {
    private String timeRange;
    private String title;
    private String description;
    private String icon;
    private String iconColorClass;
    private String iconBgClass;
    private String titleClass;
    private boolean isGoldenHour;
}
