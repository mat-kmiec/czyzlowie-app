package pl.czyzlowie.modules.fish.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

/**
 * Represents Polish Anglers Association (PZW) regulations for a specific fish species.
 *
 * The PzwRegulations class is used to define legal or recommended fishing rules and
 * restrictions for a specific fish species under the Polish Anglers Association guidelines.
 * This class can be embedded in entities where a detailed description of regulations is required.
 *
 * Fields include:
 * - Minimum and maximum dimensions allowed for fish.
 * - Exceptions for dimension regulations.
 * - Protection periods when fish cannot be caught.
 * - Spawning seasons for the fish species.
 * - Daily catch limits in terms of quantity and weight.
 * - Shared limit groups for grouped regulations.
 * - Additional rules or notes for the given fish species.
 *
 * This class is annotated as {@code @Embeddable} for use as an embedded entity.
 */
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
