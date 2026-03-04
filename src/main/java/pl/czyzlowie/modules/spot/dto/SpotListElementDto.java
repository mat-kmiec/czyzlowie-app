package pl.czyzlowie.modules.spot.dto;

import lombok.Builder;
import lombok.Data;
import pl.czyzlowie.modules.map.entity.SpotType;

/**
 * A data transfer object (DTO) representing a summarized element of a spot list.
 * This class contains minimal information about a spot to be used in list views or
 * simplified display scenarios.
 *
 * The attributes contained in this class include:
 * - id: A unique identifier for the spot.
 * - name: The name of the spot.
 * - slug: A human-readable, URL-friendly version of the spot name.
 * - spotType: The type of the spot, represented by the SpotType enum.
 * - province: The administrative province where the spot is located.
 * - nearestCity: The nearest city to the spot.
 */
@Data
@Builder
public class SpotListElementDto {

    private String id;
    private String name;
    private String slug;
    private SpotType spotType;
    private String province;
    private String nearestCity;
}
