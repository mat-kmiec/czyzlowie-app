package pl.czyzlowie.modules.moon.mapper;

import org.shredzone.commons.suncalc.MoonIllumination;
import pl.czyzlowie.modules.moon.entity.Enums.MoonPhaseType;

public class MoonPhaseMapper {

        public static MoonPhaseType mapToPhaseType(MoonIllumination illumination) {
            double angle = illumination.getPhase();
            double fractionPct = illumination.getFraction() * 100;

            if (fractionPct >= 99.5) return MoonPhaseType.FULL_MOON;
            if (fractionPct <= 0.5) return MoonPhaseType.NEW_MOON;

            if(angle >= 0){
                if(angle < 45) return MoonPhaseType.WAXING_CRESCENT;
                if(angle < 135) return MoonPhaseType.FIRST_QUARTER;
                return MoonPhaseType.WAXING_GIBBOUS;
            }else {
                if(angle > -45) return MoonPhaseType.WANING_CRESCENT;
                if(angle > -135) return MoonPhaseType.LAST_QUARTER;
                return MoonPhaseType.WANING_GIBBOUS;
            }
        }
}
