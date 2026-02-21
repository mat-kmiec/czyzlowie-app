package pl.czyzlowie.modules.spot.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import pl.czyzlowie.modules.map.entity.SpotType;

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
