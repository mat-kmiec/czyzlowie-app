package pl.czyzlowie.modules.spot.dto;

import lombok.Data;
import pl.czyzlowie.modules.map.entity.SpotType;

@Data
public class SpotFilterDto {
    private String name;
    private String province;
    private SpotType spotType;
    private String nearestCity;
}
