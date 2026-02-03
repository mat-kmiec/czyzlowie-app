package pl.czyzlowie.modules.imgw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.czyzlowie.modules.imgw.client.dto.*;
import pl.czyzlowie.modules.imgw.entity.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Mapper(componentModel = "spring")
public interface ImgwMapper {

    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Mapping(target = "id", source = "stationId")
    @Mapping(target = "name", source = "stationName")
    @Mapping(target = "latitude", ignore = true)
    @Mapping(target = "longitude", ignore = true)
    ImgwSynopStation toSynopStation(ImgwSynopResponseDto dto);

    @Mapping(target = "measurementDate", source = "measurementDate", qualifiedByName = "parseDate")
    @Mapping(target = "measurementHour", source = "measurementHour", qualifiedByName = "parseInteger")
    @Mapping(target = "temperature", source = "temperature", qualifiedByName = "parseDecimal")
    @Mapping(target = "windSpeed", source = "windSpeed", qualifiedByName = "parseInteger")
    @Mapping(target = "windDirection", source = "windDirection", qualifiedByName = "parseInteger")
    @Mapping(target = "relativeHumidity", source = "relativeHumidity", qualifiedByName = "parseDecimal")
    @Mapping(target = "totalPrecipitation", source = "totalPrecipitation", qualifiedByName = "parseDecimal")
    @Mapping(target = "pressure", source = "pressure", qualifiedByName = "parseDecimal")
    @Mapping(target = "station", ignore = true)
    ImgwSynopData toSynopData(ImgwSynopResponseDto dto);

    @Mapping(target = "id", source = "stationId")
    @Mapping(target = "name", source = "stationName")
    @Mapping(target = "latitude", source = "latitude", qualifiedByName = "parseDecimal")
    @Mapping(target = "longitude", source = "longitude", qualifiedByName = "parseDecimal")
    ImgwMeteoStation toMeteoStation(ImgwMeteoResponseDto dto);

    @Mapping(target = "airTemp", source = "airTemp", qualifiedByName = "parseDecimal")
    @Mapping(target = "airTempTime", source = "airTempTime", qualifiedByName = "parseDateTime")
    @Mapping(target = "groundTemp", source = "groundTemp", qualifiedByName = "parseDecimal")
    @Mapping(target = "groundTempTime", source = "groundTempTime", qualifiedByName = "parseDateTime")
    @Mapping(target = "windDirection", source = "windDirection", qualifiedByName = "parseInteger")
    @Mapping(target = "windAvgSpeed", source = "windAvgSpeed", qualifiedByName = "parseDecimal")
    @Mapping(target = "windMaxSpeed", source = "windMaxSpeed", qualifiedByName = "parseDecimal")
    @Mapping(target = "windMeasurementTime", source = "windMeasurementTime", qualifiedByName = "parseDateTime")
    @Mapping(target = "windGust10min", source = "windGust10min", qualifiedByName = "parseDecimal")
    @Mapping(target = "windGust10minTime", source = "windGust10minTime", qualifiedByName = "parseDateTime")
    @Mapping(target = "relativeHumidity", source = "relativeHumidity", qualifiedByName = "parseDecimal")
    @Mapping(target = "relativeHumidityTime", source = "relativeHumidityTime", qualifiedByName = "parseDateTime")
    @Mapping(target = "precipitation10min", source = "precipitation10min", qualifiedByName = "parseDecimal")
    @Mapping(target = "precipitation10minTime", source = "precipitation10minTime", qualifiedByName = "parseDateTime")
    @Mapping(target = "station", ignore = true)
    ImgwMeteoData toMeteoData(ImgwMeteoResponseDto dto);

    @Mapping(target = "id", source = "stationId")
    @Mapping(target = "name", source = "stationName")
    @Mapping(target = "latitude", source = "latitude", qualifiedByName = "parseDecimal")
    @Mapping(target = "longitude", source = "longitude", qualifiedByName = "parseDecimal")
    ImgwHydroStation toHydroStation(ImgwHydroResponseDto dto);

    @Mapping(target = "waterLevel", source = "waterLevel", qualifiedByName = "parseInteger")
    @Mapping(target = "waterLevelDate", source = "waterLevelDate", qualifiedByName = "parseDateTime")
    @Mapping(target = "waterTemperature", source = "waterTemperature", qualifiedByName = "parseDecimal")
    @Mapping(target = "waterTemperatureDate", source = "waterTemperatureDate", qualifiedByName = "parseDateTime")
    @Mapping(target = "discharge", source = "discharge", qualifiedByName = "parseDecimal")
    @Mapping(target = "dischargeDate", source = "dischargeDate", qualifiedByName = "parseDateTime")
    @Mapping(target = "icePhenomenon", source = "icePhenomenon", qualifiedByName = "parseInteger")
    @Mapping(target = "icePhenomenonDate", source = "icePhenomenonDate", qualifiedByName = "parseDateTime")
    @Mapping(target = "overgrowthPhenomenon", source = "overgrowthPhenomenon", qualifiedByName = "parseInteger")
    @Mapping(target = "overgrowthPhenomenonDate", source = "overgrowthPhenomenonDate", qualifiedByName = "parseDateTime")
    @Mapping(target = "station", ignore = true)
    ImgwHydroData toHydroData(ImgwHydroResponseDto dto);



    @Named("parseDecimal")
    default BigDecimal parseDecimal(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) { return null; }
    }

    @Named("parseInteger")
    default Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            if(value.contains(".")) {
                return new BigDecimal(value).intValue();
            }
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) { return null; }
    }

    @Named("parseDateTime")
    default LocalDateTime parseDateTime(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return LocalDateTime.parse(value.trim(), DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) { return null; }
    }

    @Named("parseDate")
    default LocalDate parseDate(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(value.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) { return null; }
    }
}