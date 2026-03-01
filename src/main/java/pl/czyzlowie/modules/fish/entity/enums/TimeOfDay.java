package pl.czyzlowie.modules.fish.entity.enums;

/**
 * Represents different periods of the day and their influence on fish activity and behavior.
 *
 * This enum is intended to provide a standardized categorization of time-based environmental
 * factors for use across various application modules such as activity forecasting, tackle
 * recommendations, and fish behavior modeling. Different species exhibit varying activity
 * levels depending on the time of day due to changes in light, temperature, and predation risks.
 *
 * Values:
 * - DAWN: The transition from night to day, characterized by low light conditions and often
 *   heightened activity in both predators and prey.
 * - DAY: The main daylight period, usually associated with normal feeding and movement patterns.
 * - DUSK: The transition from day to night, featuring low light conditions similar to dawn,
 *   triggering activity in certain nocturnal and crepuscular species.
 * - MORNING: The early portion of the day before midday, often a prime time for fishing due to
 *   cooler water temperatures and increased fish movement.
 * - NIGHT: The nocturnal period; while most fish species reduce activity, some, such as catfish,
 *   exhibit peak feeding behaviors during this time.
 * - ANY: Indicates that a species or scenario is not significantly influenced by a specific time
 *   of day and exhibits consistent behavior throughout the daily cycle.
 */
public enum TimeOfDay {
    DAWN,
    DAY,
    DUSK,
    MORNING,
    NIGHT,
    ANY
}
