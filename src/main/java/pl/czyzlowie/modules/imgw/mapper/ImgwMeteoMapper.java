package pl.czyzlowie.modules.imgw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.czyzlowie.modules.imgw.client.dto.ImgwMeteoResponseDto;
import pl.czyzlowie.modules.imgw.entity.ImgwMeteoData;
import pl.czyzlowie.modules.imgw.entity.ImgwMeteoStation;

@Mapper(componentModel = "spring", uses = ImgwTypeConverter.class)
public interface ImgwMeteoMapper {

    @Mapping(target = "id", source = "stationId")
    @Mapping(target = "name", source = "stationName")
    @Mapping(target = "latitude", qualifiedByName = "parseDecimal")
    @Mapping(target = "longitude", qualifiedByName = "parseDecimal")
    ImgwMeteoStation toMeteoStation(ImgwMeteoResponseDto dto);

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
