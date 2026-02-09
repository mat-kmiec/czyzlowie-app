package pl.czyzlowie.modules.forecast.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import pl.czyzlowie.modules.forecast.client.dto.OpenMeteoLightResponse;
import pl.czyzlowie.modules.forecast.client.dto.OpenMeteoResponse;
import pl.czyzlowie.modules.forecast.entity.VirtualStation;
import pl.czyzlowie.modules.forecast.entity.VirtualStationData;
import pl.czyzlowie.modules.forecast.entity.WeatherForecast;
import pl.czyzlowie.modules.imgw.entity.ImgwSynopStation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WeatherForecastMapper {

    DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "synopStation", ignore = true)
    @Mapping(target = "virtualStation", ignore = true)
    @Mapping(target = "forecastTime", ignore = true)
    void updateForecast(@MappingTarget WeatherForecast target, WeatherForecast source);

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
        var daily = dto.getDaily();
        int size = hourly.getTime().size();
        List<WeatherForecast> list = new ArrayList<>(size);
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < size; i++) {
            WeatherForecast entity = new WeatherForecast();

            // --- MAPOWANIE HOURLY (to co miałeś) ---
            String timeStr = getSafe(hourly.getTime(), i);
            if (timeStr != null) {
                LocalDateTime forecastTime = LocalDateTime.parse(timeStr, ISO_FORMATTER);
                entity.setForecastTime(forecastTime);

                // --- NOWOŚĆ: MAPOWANIE DAILY DO KAŻDEJ GODZINY ---
                if (daily != null && daily.getTime() != null) {
                    // Szukamy indeksu dnia w tablicy daily na podstawie daty prognozy godzinowej
                    int dayIndex = findDayIndex(daily.getTime(), forecastTime);
                    if (dayIndex != -1) {
                        entity.setSunrise(parseDateTime(getSafe(daily.getSunrise(), dayIndex)));
                        entity.setSunset(parseDateTime(getSafe(daily.getSunset(), dayIndex)));
                        entity.setUvIndexMax(toBigDecimal(getSafe(daily.getUvIndexMax(), dayIndex)));
                        // Jeśli dodasz moon_phase do DTO, odkomentuj poniższe:
                        // entity.setMoonPhase(toBigDecimal(getSafe(daily.getMoonPhase(), dayIndex)));
                    }
                }
            }

            entity.setFetchedAt(now);
            entity.setTemperature(toBigDecimal(getSafe(hourly.getTemperature2m(), i)));
            entity.setPressure(toBigDecimal(getSafe(hourly.getSurfacePressure(), i)));
            entity.setWindSpeed(toBigDecimal(getSafe(hourly.getWindSpeed10m(), i)));
            entity.setWindGusts(toBigDecimal(getSafe(hourly.getWindGusts10m(), i)));
            entity.setRain(toBigDecimal(getSafe(hourly.getRain(), i)));
            entity.setUvIndex(toBigDecimal(getSafe(hourly.getUvIndex(), i)));
            entity.setApparentTemperature(toBigDecimal(getSafe(hourly.getApparentTemperature(), i)));
            entity.setWindDirection(getSafe(hourly.getWindDirection10m(), i));
            entity.setCloudCover(getSafe(hourly.getCloudCover(), i));
            entity.setWeatherCode(getSafe(hourly.getWeatherCode(), i));

            list.add(entity);
        }
        return list;
    }

    default VirtualStationData toVirtualStationData(OpenMeteoLightResponse dto, VirtualStation station) {
        if (dto == null || dto.getCurrent() == null) {
            return null;
        }

        var current = dto.getCurrent();
        VirtualStationData entity = new VirtualStationData();
        entity.setVirtualStation(station);
        entity.setFetchedAt(LocalDateTime.now());

        if (current.getTime() != null) {
            LocalDateTime apiTime = LocalDateTime.parse(current.getTime(), ISO_FORMATTER);
            entity.setMeasurementTime(apiTime.truncatedTo(ChronoUnit.HOURS));
        } else {
            entity.setMeasurementTime(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS));
        }

        entity.setTemperature(toBigDecimal(current.getTemperature()));
        entity.setPressure(toBigDecimal(current.getPressure()));
        entity.setWindSpeed(toBigDecimal(current.getWindSpeed()));
        entity.setWindGusts(toBigDecimal(current.getWindGusts()));
        entity.setRain(toBigDecimal(current.getRain()));
        entity.setApparentTemperature(toBigDecimal(current.getApparentTemperature()));
        entity.setHumidity(toBigDecimal(current.getHumidity()));

        entity.setWindDirection(current.getWindDirection());
        entity.setWeatherCode(current.getWeatherCode());

        return entity;
    }

    private <T> T getSafe(List<T> list, int index) {
        if (list != null && index >= 0 && index < list.size()) {
            return list.get(index);
        }
        return null;
    }

    private BigDecimal toBigDecimal(Double value) {
        return value != null ? BigDecimal.valueOf(value) : null;
    }

    private BigDecimal toBigDecimal(Integer value) {
        return value != null ? BigDecimal.valueOf(value) : null;
    }

    // Dodaj te metody pomocnicze do mappera:
    private int findDayIndex(List<String> days, LocalDateTime forecastTime) {
        String targetDate = forecastTime.toLocalDate().toString(); // "2026-02-09"
        for (int i = 0; i < days.size(); i++) {
            if (targetDate.equals(days.get(i))) return i;
        }
        return -1;
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        return (dateTimeStr != null) ? LocalDateTime.parse(dateTimeStr, ISO_FORMATTER) : null;
    }
}