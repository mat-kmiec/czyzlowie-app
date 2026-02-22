package pl.czyzlowie.modules.fish.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
