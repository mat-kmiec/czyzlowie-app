package pl.czyzlowie.modules.moon.service;

import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.moon.dto.SolunarActivity;
import pl.czyzlowie.modules.moon.entity.Enums.MoonPhaseType;

@Component
public class SolunarCalculator {

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

