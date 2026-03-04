package pl.czyzlowie.modules.spot.dto;

import lombok.Data;
import pl.czyzlowie.modules.map.entity.SpotType;

/**
 * A data transfer object (DTO) for filtering information about fishing or recreational spots.
 * This class allows for specifying filter criteria to retrieve a subset of spots based
 * on the provided properties.
 *
 * The attributes include:
 * - {@code name}: The name of the spot to filter by.
 * - {@code province}: The province in which the spot is located.
 * - {@code spotType}: The type of the spot, as defined in the {@code SpotType} enumeration.
 * - {@code nearestCity}: The name of the nearest city to the spot.
 */
@Data
public class SpotFilterDto {
    private String name;
    private String province;
    private SpotType spotType;
    private String nearestCity;
}
