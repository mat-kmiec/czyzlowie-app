package pl.czyzlowie.modules.spot.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import pl.czyzlowie.modules.map.entity.SpotType;

/**
 * A data transfer object (DTO) representing generic details about a spot.
 * This class serves as an abstract base class to provide common properties
 * shared among different types of spots, such as fishing spots, lake spots,
 * or commercial spots. It is intended to be extended by more specific DTO
 * classes that include additional attributes relevant to specific spot types.
 *
 * Attributes:
 * - id: A unique identifier for the spot.
 * - name: The name of the spot.
 * - slug: A URL-friendly identifier for the spot.
 * - spotType: The type of spot (e.g., fishing, lake, commercial).
 * - latitude: The latitude coordinate of the spot.
 * - longitude: The longitude coordinate of the spot.
 * - province: The province or region where the spot is located.
 * - nearestCity: The nearest city to the spot.
 * - description: A detailed description of the spot.
 * - manager: The person or entity managing or responsible for the spot.
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public abstract class SpotDetailsDto {
    private Long id;
    private String name;
    private String slug;
    private SpotType spotType;
    private Double latitude;
    private Double longitude;
    private String province;
    private String nearestCity;
    private String description;
    private String manager;
}
