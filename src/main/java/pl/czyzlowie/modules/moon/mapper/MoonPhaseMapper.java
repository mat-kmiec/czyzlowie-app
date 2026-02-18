package pl.czyzlowie.modules.moon.mapper;
import pl.czyzlowie.modules.moon.entity.Enums.MoonPhaseType;


public class MoonPhaseMapper {

    public static MoonPhaseType calculateDailyPhase(double angle1, double angle2) {
        if (angle1 <= 0 && angle2 > 0) return MoonPhaseType.FULL_MOON;
        if (angle1 <= -90 && angle2 > -90) return MoonPhaseType.FIRST_QUARTER;
        if (angle1 <= 90 && angle2 > 90) return MoonPhaseType.LAST_QUARTER;
        if (angle1 > 90 && angle2 < -90) return MoonPhaseType.NEW_MOON;
        if (angle1 >= -180 && angle1 <= -90) return MoonPhaseType.WAXING_CRESCENT;
        if (angle1 > -90 && angle1 <= 0)     return MoonPhaseType.WAXING_GIBBOUS;
        if (angle1 > 0 && angle1 <= 90)      return MoonPhaseType.WANING_GIBBOUS;

        return MoonPhaseType.WANING_CRESCENT;
    }
}
