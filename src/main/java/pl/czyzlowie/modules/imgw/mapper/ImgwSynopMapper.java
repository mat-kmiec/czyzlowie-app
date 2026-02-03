package pl.czyzlowie.modules.imgw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.czyzlowie.modules.imgw.client.dto.ImgwSynopResponseDto;
import pl.czyzlowie.modules.imgw.entity.ImgwSynopData;
import pl.czyzlowie.modules.imgw.entity.ImgwSynopStation;

@Mapper(componentModel = "spring", uses = ImgwTypeConverter.class)
public interface ImgwSynopMapper {

    @Mapping(target = "id", source = "stationId")
    @Mapping(target = "name", source = "stationName")
    @Mapping(target = "latitude", ignore = true)
    @Mapping(target = "longitude", ignore = true)
    ImgwSynopStation toSynopStation(ImgwSynopResponseDto dto);

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