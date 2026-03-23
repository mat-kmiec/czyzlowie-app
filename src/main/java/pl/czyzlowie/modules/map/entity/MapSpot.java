package pl.czyzlowie.modules.map.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Represents an abstract entity for a geographical location or point of interest on a map.
 * This class serves as a base for various specific types of map spots with additional properties
 * and behaviors.
 *
 * This entity is mapped to the "map_spots" database table and uses a joined inheritance strategy
 * to accommodate different types of spot entities. A discriminator column named "spot_type" is
 * used to differentiate between various types of spots.
 *
 * Attributes:
 * - id: The unique identifier for the map spot, automatically generated.
 * - name: The name of the location or spot.
 * - slug: A unique and URL-friendly identifier for the location.
 * - spotType: The type of the spot, based on the SpotType enumeration.
 * - latitude: The geographical latitude of the location.
 * - longitude: The geographical longitude of the location.
 * - province: The province or administrative region where the location is situated.
 * - nearestCity: The nearest city to the location.
 * - description: A detailed description of the location, stored as text.
 * - manager: Name or identifier of the person or entity managing the location.
 */
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