package pl.czyzlowie.modules.spot.mapper;

import org.mapstruct.Mapper;
import pl.czyzlowie.modules.map.entity.MapSpot;
import pl.czyzlowie.modules.spot.dto.SpotListElementDto;

/**
 * SpotListMapper provides methods for mapping domain entity objects of type {@link MapSpot}
 * to data transfer objects (DTOs) of type {@link SpotListElementDto}.
 *
 * This interface is a MapStruct mapper, designed to facilitate the conversion of
 * entities to DTOs for use in higher application layers, such as service or controller layers.
 *
 * The mapping is primarily used to transform {@link MapSpot} objects into a more lightweight
 * form, {@link SpotListElementDto}, designed for use in list displays or summary views.
 *
 * It performs the following operations during the mapping process:
 * - Extracts and converts the id of a spot to a string.
 * - Maps basic attributes such as name, slug, description, and location details.
 * - Preserves the structural and type integrity of the entity's data for the DTO.
 *
 * By default, the mapping implementation is automatically generated at runtime
 * from the annotated interface using the MapStruct framework, following the
 * component model specified (Spring, in this case).
 */
@Mapper(componentModel = "spring")
public interface SpotListMapper {

    SpotListElementDto toDto(MapSpot spot);
}
