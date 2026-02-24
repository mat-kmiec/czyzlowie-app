package pl.czyzlowie.modules.moon.dto;

import lombok.Builder;
import lombok.Data;
import pl.czyzlowie.modules.moon.entity.Enums.MoonPhaseType;
import java.time.LocalDate;

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
