package pl.czyzlowie.modules.moon.mapper;
import pl.czyzlowie.modules.moon.entity.Enums.MoonPhaseType;


/**
 * Utility class responsible for mapping angular Moon phases to corresponding {@link MoonPhaseType}
 * based on the provided angular positions of the Moon.
 *
 * This class provides a static method to calculate the moon phase type for a specific day,
 * using angular phase values that represent the Moon's position during the current and
 * following days.
 */
public class MoonPhaseMapper {

    /**
     * Determines the Moon's phase for a given day based on two angular phases
     * provided as input arguments.
     *
     * @param angle1 the angular phase of the Moon on the current day, in degrees
     * @param angle2 the angular phase of the Moon on the following day, in degrees
     * @return the {@link MoonPhaseType} corresponding to the Moon's phase based on the provided angles
     */
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
