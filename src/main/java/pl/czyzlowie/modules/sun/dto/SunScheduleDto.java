package pl.czyzlowie.modules.sun.dto;

import lombok.Builder;
import lombok.Getter;
import pl.czyzlowie.modules.sun.dto.TimelineEventDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class SunScheduleDto {
    private LocalDateTime sunrise;
    private LocalDateTime sunset;
    private LocalDateTime zenith;
    private LocalDateTime nauticalDawn;
    private LocalDateTime civilDawn;
    private LocalDateTime civilDusk;
    private LocalDateTime nauticalDusk;
    private LocalDateTime morningGoldenEnd;
    private LocalDateTime eveningGoldenStart;
    private String sunriseTime;
    private String sunsetTime;
    private String dayLengthFormatted;
    private String dayLengthDifferenceFormatted;
    private String timeToSunsetFormatted;
    private boolean isDaylight;
    private boolean isToday;
    private List<TimelineEventDto> timeline;
}