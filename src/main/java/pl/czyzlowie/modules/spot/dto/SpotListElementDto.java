package pl.czyzlowie.modules.spot.dto;

import lombok.Builder;
import lombok.Data;
import pl.czyzlowie.modules.map.entity.SpotType;

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
