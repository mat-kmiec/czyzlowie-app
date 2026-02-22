package pl.czyzlowie.modules.fish.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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