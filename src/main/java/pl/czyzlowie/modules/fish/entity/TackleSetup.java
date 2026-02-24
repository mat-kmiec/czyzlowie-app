package pl.czyzlowie.modules.fish.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

/**
 * Represents an embedded configuration for fishing tackle setup.
 *
 * The TackleSetup class is designed to store detailed information about
 * the fishing tackle configurations. It can define bait preferences,
 * equipment details such as rods, reels, lines, hooks, and terminal tackle.
 *
 * This class is annotated as {@code @Embeddable} for use as an embeddable
 * component within an entity, allowing it to be included in other
 * entity classes as a part of their attributes.
 *
 * Fields in this class include:
 * - {@code killerBaits}: Stores textual data regarding recommended killer baits.
 * - {@code baitSelectionRules}: Contains textual information on bait selection rules.
 * - {@code setupRod}: Describes the rod used in the setup.
 * - {@code setupReel}: Describes the reel used in the setup.
 * - {@code setupMainLine}: Specifies the main line type in the setup.
 * - {@code setupLeader}: Specifies the leader line type in the setup.
 * - {@code setupHook}: Defines the hook type used in the setup.
 * - {@code setupTerminalTackle}: Contains the terminal tackle details.
 *
 * Annotations Used:
 * - {@code @Embeddable}: Indicates this class is meant to be embedded into an entity.
 * - {@code @Column}: Used for custom database column configurations of the fields.
 * - {@code @Data}: From Lombok, auto-generates getters, setters, and other utility methods.
 */
@Embeddable
@Data
public class TackleSetup {
    @Column(columnDefinition = "TEXT")
    private String killerBaits;

    @Column(columnDefinition = "TEXT")
    private String baitSelectionRules;

    private String setupRod;
    private String setupReel;
    private String setupMainLine;
    private String setupLeader;
    private String setupHook;
    private String setupTerminalTackle;
}
