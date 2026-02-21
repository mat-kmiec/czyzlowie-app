package pl.czyzlowie.modules.map.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "map_spots")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "spot_type", discriminatorType = DiscriminatorType.STRING)
@Getter @Setter
public abstract class MapSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(name = "spot_type", insertable = false, updatable = false)
    private SpotType spotType;

    private Double latitude;
    private Double longitude;

    private String province;
    private String nearestCity;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String manager;
}