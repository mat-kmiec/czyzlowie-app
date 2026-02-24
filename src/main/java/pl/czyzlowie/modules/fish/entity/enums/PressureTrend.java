package pl.czyzlowie.modules.fish.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Defines the direction and stability of atmospheric pressure and its influence on fish activity.
 * * Barometric pressure is a critical factor in the forecasting algorithm as it affects
 * the swim bladder of many species, thereby influencing their depth, comfort, and
 * willingness to feed. This enum provides both a human-readable name and technical
 * hints for the scoring engine.
 * * Values:
 * - RISING: Pressure is increasing; often excellent after a long period of low pressure.
 * - FALLING: Pressure is decreasing; signals weather changes and often triggers
 * intense feeding "frenzies" in predators.
 * - STABLE_HIGH: Consistent high pressure; ideal for deep-water species like Zander.
 * - STABLE_LOW: Consistent low pressure; typically the weakest period for most species.
 * - FLUCTUATING: Sharp changes in pressure; disorientates peaceful fish but may
 * stimulate aggressive behavior in Perch.
 * - ANY: Indicates the species is resilient to barometric changes.
 */
@Getter
@RequiredArgsConstructor
public enum PressureTrend {
    RISING("Rosnące", "Najlepsze po długim okresie niskiego ciśnienia."),
    FALLING("Spadające", "Zwiastuje załamanie pogody, często wyzwala szał żerowania drapieżników."),
    STABLE_HIGH("Stabilne Wysokie", "Idealne dla sandacza i ryb głębinowych."),
    STABLE_LOW("Stabilne Niskie", "Zazwyczaj najsłabszy okres na większość gatunków."),
    FLUCTUATING("Skoki ciśnienia", "Dezorientuje białoryb, ale może pobudzić okonia."),
    ANY("Bez znaczenia", "Gatunek odporny na zmiany barometryczne.");

    private final String displayName;
    private final String algorithmHint;
}