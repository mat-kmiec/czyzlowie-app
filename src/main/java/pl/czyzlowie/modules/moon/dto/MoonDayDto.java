package pl.czyzlowie.modules.moon.dto;

import lombok.Builder;
import lombok.Data;
import pl.czyzlowie.modules.moon.entity.Enums.MoonPhaseType;
import java.time.LocalDate;

/**
 * Data Transfer Object representing information about a specific day in the Moon calendar.
 * This class encapsulates details such as the date, moon day, phase, solunar activity,
 * and attributes related to the Moon's characteristics for that particular day.
 */
@Data
@Builder
public class MoonDayDto {
    private LocalDate date;
    private int dayOfMonth;
    private MoonPhaseType phase;
    private String phaseNamePl;
    private SolunarActivity activity;
    private boolean isToday;
    private boolean isSuperMoon;
    private boolean isEmpty;
}
