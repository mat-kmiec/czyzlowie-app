package pl.czyzlowie.modules.moon.entity.Enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the different phases of the Moon as enumerated types.
 * Each phase is associated with a localized name in Polish.
 */
@Getter
@AllArgsConstructor
public enum MoonPhaseType {
    NEW_MOON("Nów"),
    WAXING_CRESCENT("Przybywający Sierp"),
    FIRST_QUARTER("Pierwsza Kwadra"),
    WAXING_GIBBOUS("Przybywający Garb"),
    FULL_MOON("Pełnia"),
    WANING_GIBBOUS("Ubywający Garb"),
    LAST_QUARTER("Ostatnia Kwadra"),
    WANING_CRESCENT("Ubywający Sierp");

    private final String name;
}
