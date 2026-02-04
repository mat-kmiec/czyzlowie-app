package pl.czyzlowie.modules.imgw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.czyzlowie.modules.imgw.client.dto.ImgwSynopResponseDto;
import pl.czyzlowie.modules.imgw.entity.ImgwSynopData;
import pl.czyzlowie.modules.imgw.entity.ImgwSynopStation;

/**
 * The {@code ImgwSynopMapper} interface is a MapStruct mapper designed to map data
 * between {@link ImgwSynopResponseDto} and domain entities such as
 * {@link ImgwSynopStation} and {@link ImgwSynopData}.
 *
 * This mapper provides methods for converting API response data to internal data models,
 * adhering to custom mapping rules including value transformations and field exclusions.
 * It relies on the {@link ImgwTypeConverter} utility for specialized field conversions such as
 * parsing dates, numbers, and other data types.
 *
 * The mapper is annotated with {@code @Mapper} to generate the implementation at compile time.
 * The generated implementation is Spring-integrated, enabling dependency injection.
 */
@Mapper(componentModel = "spring", uses = ImgwTypeConverter.class)
public interface ImgwSynopMapper {

    /**
     * Maps an {@link ImgwSynopResponseDto} object to an {@link ImgwSynopStation} entity.
     * This method transfers selected fields from the source DTO to the target entity.
     *
     * @param dto the {@link ImgwSynopResponseDto} containing the source data
     * @return an {@link ImgwSynopStation} entity with mapped data from the given DTO
     */
    @Mapping(target = "id", source = "stationId")
    @Mapping(target = "name", source = "stationName")
    @Mapping(target = "latitude", ignore = true)
    @Mapping(target = "longitude", ignore = true)
    ImgwSynopStation toSynopStation(ImgwSynopResponseDto dto);

    /**
     * Maps an {@link ImgwSynopResponseDto} object to an {@link ImgwSynopData} entity.
     * This method converts input data from DTO format to the entity format,
     * applying specific parsing or transformation rules for individual fields.
     *
     * @param dto the {@link ImgwSynopResponseDto} containing raw synoptic data to be mapped
     * @return an {@link ImgwSynopData} entity with parsed and mapped data
     */
    @Mapping(target = "measurementDate", qualifiedByName = "parseDate")
    @Mapping(target = "measurementHour", qualifiedByName = "parseInteger")
    @Mapping(target = "temperature", qualifiedByName = "parseDecimal")
    @Mapping(target = "windSpeed", qualifiedByName = "parseInteger")
    @Mapping(target = "windDirection", qualifiedByName = "parseInteger")
    @Mapping(target = "relativeHumidity", qualifiedByName = "parseDecimal")
    @Mapping(target = "totalPrecipitation", qualifiedByName = "parseDecimal")
    @Mapping(target = "pressure", qualifiedByName = "parseDecimal")
    @Mapping(target = "station", ignore = true)
    ImgwSynopData toSynopData(ImgwSynopResponseDto dto);
}