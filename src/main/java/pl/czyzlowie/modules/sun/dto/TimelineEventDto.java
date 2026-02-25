package pl.czyzlowie.modules.sun.dto;

import lombok.Builder;
import lombok.Getter;

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
