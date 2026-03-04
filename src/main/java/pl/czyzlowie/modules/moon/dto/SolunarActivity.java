package pl.czyzlowie.modules.moon.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing different levels of solunar activity, which can be used
 * to determine the intensity of biological activity based on lunar and solar positions.
 * Each activity level is associated with a numerical rank, a descriptive string in Polish,
 * and a specific CSS class for styling purposes.
 *
 * The enum constants include:
 * - VERY_POOR: Represents minimal activity, with the lowest level.
 * - POOR: Represents low activity, requiring more strategic efforts.
 * - AVERAGE: Represents standard activity levels.
 * - GOOD: Represents increasing activity levels.
 * - EXCELLENT: Represents the peak of solunar activity.
 *
 * Fields:
 * - level: Numeric rank indicating the level of solunar activity.
 * - description: A string description of the activity level in Polish.
 * - cssClass: A string representing a CSS class for styling the associated activity level.
 */
@Getter
@AllArgsConstructor
public enum SolunarActivity {
    VERY_POOR(1, "Bardzo Słaba (Pustynia w wodzie)", "text-danger"),
    POOR(2, "Słaba (Wymaga kombinowania)", "text-warning"),
    AVERAGE(3, "Średnia (Standardowe żerowanie)", "text-info"),
    GOOD(4, "Dobra (Rosnąca aktywność)", "text-primary"),
    EXCELLENT(5, "Doskonała (Szczyt solunarny)", "text-success");

    private final int level;
    private final String description;
    private final String cssClass;
}