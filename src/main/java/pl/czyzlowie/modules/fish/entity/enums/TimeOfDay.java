package pl.czyzlowie.modules.fish.entity.enums;

/**
 * Represents the specific period within a 24-hour cycle and its relevance to fish feeding patterns.
 *
 * This enum is utilized by the algorithm to identify the peak activity windows for
 * various species. Many fish exhibit circadian rhythms, such as being crepuscular
 * (active at dawn and dusk), nocturnal, or diurnal.
 *
 * Values:
 * - DAWN: The period of first light and sunrise, often a high-activity window for predators.
 * - DAY: The period of full daylight between sunrise and sunset.
 * - DUSK: The period of fading light and sunset, a critical transition time for many species.
 * - NIGHT: The period of darkness, preferred by nocturnal hunters like catfish or burbot.
 * - ANY: Indicates that the species can be caught with similar effectiveness at any time.
 */
public enum TimeOfDay {
    DAWN,
    DAY,
    DUSK,
    NIGHT,
    ANY
}
