package pl.czyzlowie.modules.fish.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

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
