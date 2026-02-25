package pl.czyzlowie.modules.imgw_ui.synop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.czyzlowie.modules.forecast.entity.VirtualStationData;
import pl.czyzlowie.modules.imgw_api.entity.ImgwSynopData;
import pl.czyzlowie.modules.imgw_ui.synop.dto.WeatherReadingDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface WeatherDataMapper {

    @Mapping(target = "timeLabel", expression = "java(formatSynopDateHour(source.getMeasurementDate(), source.getMeasurementHour()))")
    @Mapping(target = "timestamp", expression = "java(formatIso(source.getMeasurementDate(), source.getMeasurementHour()))")
    @Mapping(target = "humidity", source = "relativeHumidity")
    @Mapping(target = "precipitation", source = "totalPrecipitation")
    WeatherReadingDto mapSynop(ImgwSynopData source);

    @Mapping(target = "timeLabel", source = "measurementTime", qualifiedByName = "formatVirtualDateTime")
    @Mapping(target = "timestamp", expression = "java(source.getMeasurementTime() != null ? source.getMeasurementTime().toString() : null)")
    @Mapping(target = "precipitation", source = "rain")
    WeatherReadingDto mapVirtual(VirtualStationData source);

    default String formatSynopDateHour(java.time.LocalDate date, Integer hour) {
        if (date == null || hour == null) return "00:00";
        return String.format("%02d.%02d %02d:00", date.getDayOfMonth(), date.getMonthValue(), hour);
    }

    default String formatIso(java.time.LocalDate date, Integer hour) {
        if (date == null || hour == null) return null;
        if(hour == 24) return date.plusDays(1).atTime(0,0).toString();
        return date.atTime(hour, 0).toString();
    }

    @Named("formatVirtualDateTime")
    default String formatVirtualDateTime(LocalDateTime time) {
        if (time == null) return "00:00";
        return time.format(DateTimeFormatter.ofPattern("dd.MM HH:00"));
    }
}