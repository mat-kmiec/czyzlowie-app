package pl.czyzlowie.modules.user_panel.notes;

import lombok.Getter;

/**
 * The NoteCategory enum represents various categories of notes.
 * Each category has a display name and an associated icon name.
 */
@Getter
public enum NoteCategory {
    TAKTYKA("Taktyka i Rozkminy", "crosshair"),
    ZAKUPY("Braki Sprzętowe / Zakupy", "shopping-cart"),
    POGODA("Analiza Pogodowa", "cloud-lightning"),
    INNE("Zapiski Ogólne", "folder");

    private final String displayName;
    private final String iconName;

    NoteCategory(String displayName, String iconName) {
        this.displayName = displayName;
        this.iconName = iconName;
    }

}