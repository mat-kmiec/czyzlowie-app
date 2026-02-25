package pl.czyzlowie.modules.imgw_ui.hydro.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.czyzlowie.modules.imgw_api.entity.ImgwHydroData;
import pl.czyzlowie.modules.imgw_ui.hydro.dto.HydroReadingDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface HydroDataMapper {

    @Mapping(target = "timestamp", source = ".", qualifiedByName = "resolveBestTimestamp")
    @Mapping(target = "timeLabel", source = ".", qualifiedByName = "formatTimeLabel")
    @Mapping(target = "timestampIso", source = ".", qualifiedByName = "formatIsoTimestamp")
    @Mapping(target = "waterLevelDate", source = "waterLevelDate")
    @Mapping(target = "waterTemperatureDate", source = "waterTemperatureDate")
    @Mapping(target = "dischargeDate", source = "dischargeDate")
    HydroReadingDto mapHydro(ImgwHydroData source);

    @Named("resolveBestTimestamp")
    default LocalDateTime resolveBestTimestamp(ImgwHydroData source) {
        if (source.getWaterLevelDate() != null) return source.getWaterLevelDate();
        if (source.getDischargeDate() != null) return source.getDischargeDate();
        if (source.getWaterTemperatureDate() != null) return source.getWaterTemperatureDate();
        return source.getCreatedAt();
    }

    @Named("formatTimeLabel")
    default String formatTimeLabel(ImgwHydroData source) {
        LocalDateTime time = resolveBestTimestamp(source);
        if (time == null) return "Brak daty";
        return time.format(DateTimeFormatter.ofPattern("dd.MM HH:mm"));
    }

    @Named("formatIsoTimestamp")
    default String formatIsoTimestamp(ImgwHydroData source) {
        LocalDateTime time = resolveBestTimestamp(source);
        return time != null ? time.toString() : null;
    }
}
