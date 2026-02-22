package pl.czyzlowie.modules.fish.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class PzwRegulations {

    @Column(name = "pzw_min_dimension")
    private Integer minDimension;

    @Column(name = "pzw_max_dimension")
    private Integer maxDimension;

    @Column(name = "pzw_dimension_exceptions")
    private String dimensionExceptions;

    @Column(name = "pzw_protection_period")
    private String protectionPeriod;

    @Column(name = "pzw_spawning_season")
    private String spawningSeason;

    @Column(name = "pzw_daily_limit_pieces")
    private Integer dailyLimitPieces;

    @Column(name = "pzw_daily_limit_weight")
    private Double dailyLimitWeight;

    @Column(name = "pzw_shared_limit_group")
    private String sharedLimitGroup;

    @Column(name = "pzw_additional_rules", columnDefinition = "TEXT")
    private String additionalRules;
}
