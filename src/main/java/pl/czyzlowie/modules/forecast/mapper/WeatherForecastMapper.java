package pl.czyzlowie.modules.forecast.mapper;

import pl.czyzlowie.modules.forecast.client.dto.OpenMeteoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import pl.czyzlowie.modules.forecast.entity.VirtualStation;
import pl.czyzlowie.modules.forecast.entity.WeatherForecast;
import pl.czyzlowie.modules.imgw.entity.ImgwSynopStation;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WeatherForecastMapper {

    DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    default List<WeatherForecast> toSynopForecasts(OpenMeteoResponse dto, ImgwSynopStation station) {
        List<WeatherForecast> forecasts = mapCommonData(dto);
        forecasts.forEach(f -> f.setSynopStation(station));
        return forecasts;
    }

    default List<WeatherForecast> toVirtualForecasts(OpenMeteoResponse dto, VirtualStation station) {
        List<WeatherForecast> forecasts = mapCommonData(dto);
        forecasts.forEach(f -> f.setVirtualStation(station));
        return forecasts;
    }

    default List<WeatherForecast> mapCommonData(OpenMeteoResponse dto) {
        if (dto == null || dto.getHourly() == null || dto.getHourly().getTime() == null) {
            return Collections.emptyList();
        }

        var hourly = dto.getHourly();
        int size = hourly.getTime().size();
        List<WeatherForecast> list = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            WeatherForecast entity = new WeatherForecast();

            String timeStr = hourly.getTime().get(i);
            if (timeStr != null) {
                entity.setForecastTime(LocalDateTime.parse(timeStr, ISO_FORMATTER));
            }
            entity.setFetchedAt(LocalDateTime.now());

            if (hourly.getTemperature2m() != null) {
                Double val = hourly.getTemperature2m().get(i);
                if (val != null) entity.setTemperature(BigDecimal.valueOf(val));
            }

            if (hourly.getSurfacePressure() != null) {
                Double val = hourly.getSurfacePressure().get(i);
                if (val != null) entity.setPressure(BigDecimal.valueOf(val));
            }

            if (hourly.getWindSpeed10m() != null) {
                Double val = hourly.getWindSpeed10m().get(i);
                if (val != null) entity.setWindSpeed(BigDecimal.valueOf(val));
            }

            if (hourly.getWindGusts10m() != null) {
                Double val = hourly.getWindGusts10m().get(i);
                if (val != null) entity.setWindGusts(BigDecimal.valueOf(val));
            }

            if (hourly.getRain() != null) {
                Double val = hourly.getRain().get(i);
                if (val != null) entity.setRain(BigDecimal.valueOf(val));
            }

            if (hourly.getUvIndex() != null) {
                Double val = hourly.getUvIndex().get(i);
                if (val != null) entity.setUvIndex(BigDecimal.valueOf(val));
            }

            if (hourly.getApparentTemperature() != null) {
                Double val = hourly.getApparentTemperature().get(i);
                if (val != null) entity.setApparentTemperature(BigDecimal.valueOf(val));
            }

            if (hourly.getWindDirection10m() != null) {
                entity.setWindDirection(hourly.getWindDirection10m().get(i));
            }

            if (hourly.getCloudCover() != null) {
                entity.setCloudCover(hourly.getCloudCover().get(i));
            }

            if (hourly.getWeatherCode() != null) {
                entity.setWeatherCode(hourly.getWeatherCode().get(i));
            }

            list.add(entity);
        }

        return list;
    }
}