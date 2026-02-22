package pl.czyzlowie.modules.fish.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FishCategory {
    PREDATOR("Drapieżniki"),
    PEACEFUL("Białoryb (Spokojnego żeru)");

    private final String displayName;
}
