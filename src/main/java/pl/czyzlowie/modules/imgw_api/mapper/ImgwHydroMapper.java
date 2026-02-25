package pl.czyzlowie.modules.imgw_api.mapper;

import org.mapstruct.Mapper;


import org.mapstruct.Mapping;
import pl.czyzlowie.modules.imgw_api.client.dto.ImgwHydroResponseDto;
import pl.czyzlowie.modules.imgw_api.entity.ImgwHydroData;
import pl.czyzlowie.modules.imgw_api.entity.ImgwHydroStation;

/**
 * The ImgwHydroMapper interface defines mapping methods for converting instances of
 * {@link ImgwHydroResponseDto} into various domain entities, such as {@link ImgwHydroStation}
 * and {@link ImgwHydroData}. Leveraging MapStruct, it facilitates data transformation
 * between DTOs and entities within the application.
 *
 * This interface uses the MapStruct framework's annotations to specify the mapping rules
 * and type conversions. A type converter class, {@link ImgwTypeConverter}, provides
 * additional logic for complex type transformation.
 *
 * The mapping implementation is automatically generated at build-time, enabling efficient
 * and type-safe transformation without manual intervention.
 */
@Mapper(componentModel = "spring", uses = ImgwTypeConverter.class)
public interface ImgwHydroMapper {

    /**
     * Maps an instance of {@link ImgwHydroResponseDto} to an instance of {@link ImgwHydroStation}.
     * Fields from the source object are mapped and transformed to the corresponding fields
     * in the target object using predefined mappings and qualified converters.
     *
     * @param dto the {@link ImgwHydroResponseDto} instance containing source data for mapping
     * @return an {@link ImgwHydroStation} instance with mapped and transformed data
     */
    @Mapping(target = "id", source = "stationId")
    @Mapping(target = "name", source = "stationName")
    @Mapping(target = "latitude", qualifiedByName = "parseDecimal")
    @Mapping(target = "longitude", qualifiedByName = "parseDecimal")
    ImgwHydroStation toHydroStation(ImgwHydroResponseDto dto);

    /**
     * Maps an {@link ImgwHydroResponseDto} object to an {@link ImgwHydroData} entity.
     *
     * @param dto the source data transfer object containing the hydro data response information
     * @return an {@link ImgwHydroData} entity containing mapped and converted hydro data
     */
    @Mapping(target = "waterLevel", qualifiedByName = "parseInteger")
    @Mapping(target = "waterLevelDate", qualifiedByName = "parseDateTime")
    @Mapping(target = "waterTemperature", qualifiedByName = "parseDecimal")
    @Mapping(target = "waterTemperatureDate", qualifiedByName = "parseDateTime")
    @Mapping(target = "discharge", qualifiedByName = "parseDecimal")
    @Mapping(target = "dischargeDate", qualifiedByName = "parseDateTime")
    @Mapping(target = "icePhenomenon", qualifiedByName = "parseInteger")
    @Mapping(target = "icePhenomenonDate", qualifiedByName = "parseDateTime")
    @Mapping(target = "overgrowthPhenomenon", qualifiedByName = "parseInteger")
    @Mapping(target = "overgrowthPhenomenonDate", qualifiedByName = "parseDateTime")
    @Mapping(target = "station", ignore = true)
    ImgwHydroData toHydroData(ImgwHydroResponseDto dto);
}
