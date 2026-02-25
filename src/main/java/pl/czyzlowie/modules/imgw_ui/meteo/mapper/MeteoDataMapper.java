package pl.czyzlowie.modules.imgw_ui.meteo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.czyzlowie.modules.imgw_api.entity.ImgwMeteoData;
import pl.czyzlowie.modules.imgw_ui.meteo.dto.MeteoReadingDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface MeteoDataMapper {

    @Mapping(target = "timestamp", source = ".", qualifiedByName = "resolveBestTimestampMeteo")
    @Mapping(target = "timeLabel", source = ".", qualifiedByName = "formatTimeLabelMeteo")
    @Mapping(target = "timestampIso", source = ".", qualifiedByName = "formatIsoTimestampMeteo")
    @Mapping(target = "airTempDate", source = "airTempTime")
    MeteoReadingDto mapMeteo(ImgwMeteoData source);

    @Named("resolveBestTimestampMeteo")
    default LocalDateTime resolveBestTimestamp(ImgwMeteoData source) {
        if (source.getAirTempTime() != null) return source.getAirTempTime();
        if (source.getWindMeasurementTime() != null) return source.getWindMeasurementTime();
        if (source.getPrecipitation10minTime() != null) return source.getPrecipitation10minTime();
        return source.getCreatedAt();
    }

    @Named("formatTimeLabelMeteo")
    default String formatTimeLabel(ImgwMeteoData source) {
        LocalDateTime time = resolveBestTimestamp(source);
        if (time == null) return "Brak daty";
        return time.format(DateTimeFormatter.ofPattern("dd.MM HH:mm"));
    }

    @Named("formatIsoTimestampMeteo")
    default String formatIsoTimestamp(ImgwMeteoData source) {
        LocalDateTime time = resolveBestTimestamp(source);
        return time != null ? time.toString() : null;
    }
}