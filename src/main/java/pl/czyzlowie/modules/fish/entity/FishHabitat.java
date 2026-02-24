package pl.czyzlowie.modules.fish.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Represents the environmental and habitat preferences for a specific fish species.
 *
 * This class defines where the species is typically found, including specific descriptions
 * for different types of water bodies and precise physical parameters of their preferred
 * surroundings. It is intended to be embedded within the {@link FishSpecies} entity.
 *
 * Information stored in this class includes:
 * - General habitat characteristics and geographical distribution.
 * - Specific behavior and locations within standing waters (lakes) and flowing waters (rivers).
 * - Vertical distribution in the water column (preferred depth range and feeding layer).
 * - Substrate preferences (bottom type).
 *
 * The data in this class helps anglers identify the most likely spots to find a
 * particular species based on the water body's characteristics.
 */
@Embeddable
@Data
public class FishHabitat {
    @Column(columnDefinition = "TEXT")
    private String habitatGeneral;

    @Column(columnDefinition = "TEXT")
    private String habitatLakes;

    @Column(columnDefinition = "TEXT")
    private String habitatRivers;

    @Column(name = "preferred_depth_min", precision = 5, scale = 2)
    private BigDecimal preferredDepthMin;

    @Column(name = "preferred_depth_max", precision = 5, scale = 2)
    private BigDecimal preferredDepthMax;

    @Column(name = "bottom_type")
    private String bottomType;

    @Column(name = "feeding_layer")
    private String feedingLayer;
}
