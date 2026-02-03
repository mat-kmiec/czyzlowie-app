package pl.czyzlowie.modules.imgw.mapper;

import org.mapstruct.Mapper;


import org.mapstruct.Mapping;
import pl.czyzlowie.modules.imgw.client.dto.ImgwHydroResponseDto;
import pl.czyzlowie.modules.imgw.entity.ImgwHydroData;
import pl.czyzlowie.modules.imgw.entity.ImgwHydroStation;

@Mapper(componentModel = "spring", uses = ImgwTypeConverter.class)
public interface ImgwHydroMapper {

    @Mapping(target = "id", source = "stationId")
    @Mapping(target = "name", source = "stationName")
    @Mapping(target = "latitude", qualifiedByName = "parseDecimal")
    @Mapping(target = "longitude", qualifiedByName = "parseDecimal")
    ImgwHydroStation toHydroStation(ImgwHydroResponseDto dto);

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
