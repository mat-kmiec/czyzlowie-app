package pl.czyzlowie.modules.fish.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * Enum representing different categories of fish.
 * Each category contains a display name describing the type of fish it represents.
 *
 * Enum Constants:
 * - PREDATOR: Represents predatory fish.
 * - PEACEFUL: Represents peaceful fish, often referred to as whitefish.
 * - SALMONID: Represents Salmonidae and Thymallidae families.
 * - MARINE: Represents marine fish species.
 * - INVASIVE: Represents foreign and invasive fish species.
 *
 * Constructor:
 * Each enum instance is initialized with a display name describing the category.
 */
@Getter
@RequiredArgsConstructor
public enum FishCategory {
    PREDATOR("Drapieżniki"),
    PEACEFUL("Spokojnego żeru / Białoryb"),
    SALMONID("Łososiowate i Lipieniowate"),
    MARINE("Ryby morskie"),
    INVASIVE("Gatunki obce i inwazyjne");

    private final String displayName;
}
