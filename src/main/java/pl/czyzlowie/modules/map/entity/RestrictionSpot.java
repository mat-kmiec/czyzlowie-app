package pl.czyzlowie.modules.map.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "map_restrictions")
@DiscriminatorValue("RESTRICTION")
@Getter
@Setter
public class RestrictionSpot extends MapSpot {

    @Enumerated(EnumType.STRING)
    @Column(name = "restriction_type")
    private RestrictionType restrictionType;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(columnDefinition = "TEXT")
    private String polygonCoordinates;
}
