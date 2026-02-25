package pl.czyzlowie.modules.imgw_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.czyzlowie.modules.imgw_api.client.dto.ImgwMeteoResponseDto;
import pl.czyzlowie.modules.imgw_api.entity.ImgwMeteoData;
import pl.czyzlowie.modules.imgw_api.entity.ImgwMeteoStation;

/**
 * Mapper interface for converting between ImgwMeteoResponseDto, ImgwMeteoStation, and ImgwMeteoData objects.
 * Uses ImgwTypeConverter for parsing and data transformation tasks.
 * Implements field-level mapping and customization through MapStruct annotations.
 */
@Mapper(componentModel = "spring", uses = ImgwTypeConverter.class)
public interface ImgwMeteoMapper {

    /**
     * Converts an instance of ImgwMeteoResponseDto to an instance of ImgwMeteoStation.
     * Maps the fields in the source object to the corresponding fields in the target object,
     * applying transformations where necessary (e.g., parsing numeric values).
     *
     * @param dto the ImgwMeteoResponseDto instance containing the source data for mapping
     * @return an ImgwMeteoStation instance with fields mapped and transformed based on the source data
     */
    @Mapping(target = "id", source = "stationId")
    @Mapping(target = "name", source = "stationName")
    @Mapping(target = "latitude", qualifiedByName = "parseDecimal")
    @Mapping(target = "longitude", qualifiedByName = "parseDecimal")
    ImgwMeteoStation toMeteoStation(ImgwMeteoResponseDto dto);

    /**
     * Maps an instance of ImgwMeteoResponseDto to an instance of ImgwMeteoData.
     * Field values from the input object are converted and assigned to the corresponding
     * fields in the output object based on predefined mappings and converters.
     *
     * @param dto the ImgwMeteoResponseDto instance containing the source data for mapping
     * @return an ImgwMeteoData instance containing the mapped data
     */
    @Mapping(target = "airTemp", qualifiedByName = "parseDecimal")
    @Mapping(target = "airTempTime", qualifiedByName = "parseDateTime")
    @Mapping(target = "groundTemp", qualifiedByName = "parseDecimal")
    @Mapping(target = "groundTempTime", qualifiedByName = "parseDateTime")
    @Mapping(target = "windDirection", qualifiedByName = "parseInteger")
    @Mapping(target = "windAvgSpeed", qualifiedByName = "parseDecimal")
    @Mapping(target = "windMaxSpeed", qualifiedByName = "parseDecimal")
    @Mapping(target = "windMeasurementTime", qualifiedByName = "parseDateTime")
    @Mapping(target = "windGust10min", qualifiedByName = "parseDecimal")
    @Mapping(target = "windGust10minTime", qualifiedByName = "parseDateTime")
    @Mapping(target = "relativeHumidity", qualifiedByName = "parseDecimal")
    @Mapping(target = "relativeHumidityTime", qualifiedByName = "parseDateTime")
    @Mapping(target = "precipitation10min", qualifiedByName = "parseDecimal")
    @Mapping(target = "precipitation10minTime", qualifiedByName = "parseDateTime")
    @Mapping(target = "station", ignore = true)
    ImgwMeteoData toMeteoData(ImgwMeteoResponseDto dto);
}
