package pl.czyzlowie.modules.fish.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Quantifies the intensity of fish activity and feeding behavior for a given period.
 *
 * This enum is a cornerstone of the visualization and calculation layers. Each level
 * carries a numerical weight used by the forecasting engine to scale results, and a
 * hex color code used for consistent UI rendering across activity charts and calendars.
 *
 * Values:
 * - NONE: Indicates no activity or a total cessation of feeding, typically during spawning
 * or extreme weather conditions.
 * - LOW: Minimal activity; fish are difficult to locate and hesitant to bite.
 * - MEDIUM: Average activity; standard fishing conditions with moderate chances of success.
 * - HIGH: Strong activity; fish are actively patrolling and feeding.
 * - EXCELLENT: Peak activity (often referred to as "Eldorado"); rare conditions where
 * fish are in a feeding frenzy and very easy to catch.
 */
@Getter
@RequiredArgsConstructor
public enum ActivityLevel {
    NONE("Brak / Tarło", 0, "#334155"),
    LOW("Słaba", 1, "#ef4444"),
    MEDIUM("Średnia", 2, "#f59e0b"),
    HIGH("Dobra", 3, "#84cc16"),
    EXCELLENT("Eldorado", 4, "#22c55e");

    private final String displayName;
    private final int algorithmWeight;
    private final String hexColor;
}
