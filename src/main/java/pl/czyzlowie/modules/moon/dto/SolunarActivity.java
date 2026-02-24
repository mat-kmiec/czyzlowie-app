package pl.czyzlowie.modules.moon.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

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