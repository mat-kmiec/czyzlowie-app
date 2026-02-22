package pl.czyzlowie.modules.spot.mapper;

import org.mapstruct.Mapper;
import pl.czyzlowie.modules.map.entity.MapSpot;
import pl.czyzlowie.modules.spot.dto.SpotListElementDto;

@Mapper(componentModel = "spring")
public interface SpotListMapper {

    SpotListElementDto toDto(MapSpot spot);
}
