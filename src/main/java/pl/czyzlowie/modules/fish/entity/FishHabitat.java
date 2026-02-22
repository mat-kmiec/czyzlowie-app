package pl.czyzlowie.modules.fish.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.math.BigDecimal;

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
