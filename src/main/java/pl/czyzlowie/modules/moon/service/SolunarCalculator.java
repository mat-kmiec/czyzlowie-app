package pl.czyzlowie.modules.moon.service;

import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.moon.dto.SolunarActivity;
import pl.czyzlowie.modules.moon.entity.Enums.MoonPhaseType;

/**
 * The SolunarCalculator class is responsible for determining solunar activity
 * levels based on the phase of the moon. Solunar activity is often used in
 * predicting the behavior of fish and wildlife, particularly for hunting or
 * fishing purposes.
 */
@Component
public class SolunarCalculator {

    /**
     * Calculates the solunar activity based on the given moon phase.
     *
     * @param phase the moon phase for which the solunar activity is to be calculated
     * @return the calculated solunar activity level corresponding to the provided moon phase
     */
    public SolunarActivity calculateActivity(MoonPhaseType phase) {
        return switch (phase) {
            case NEW_MOON, FULL_MOON -> SolunarActivity.EXCELLENT;
            case WAXING_CRESCENT, WAXING_GIBBOUS -> SolunarActivity.GOOD;
            case FIRST_QUARTER -> SolunarActivity.AVERAGE;
            case WANING_GIBBOUS, LAST_QUARTER -> SolunarActivity.POOR;
            case WANING_CRESCENT -> SolunarActivity.VERY_POOR;
        };
    }
}

