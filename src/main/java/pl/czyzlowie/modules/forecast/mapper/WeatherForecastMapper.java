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

/**
 * Interface responsible for mapping and transforming weather forecast data between domain entities
 * and external weather API Data Transfer Objects (DTOs). The implementation makes use of MapStruct
 * to handle mapping operations.
 *
 * The main responsibilities of this interface include:
 * - Mapping and updating WeatherForecast entities based on data from external sources.
 * - Mapping weather forecast data for synoptic and virtual stations.
 * - Processing OpenMeteo responses to extract and transform relevant weather information.
 * - Handling data formatting, safe access to lists, and conversion to appropriate types for domain models.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WeatherForecastMapper {

    DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "synopStation", ignore = true)
    @Mapping(target = "virtualStation", ignore = true)
    @Mapping(target = "forecastTime", ignore = true)
    void updateForecast(@MappingTarget WeatherForecast target, WeatherForecast source);

    /**
     * Converts an OpenMeteoResponse DTO into a list of WeatherForecast objects associated with
     * a specific synoptic station.
     *
     * This method maps common weather data from the provided DTO and assigns the specified
     * synoptic station to each WeatherForecast object in the resulting list.
     *
     * @param dto the OpenMeteoResponse object containing weather data to be converted
     * @param station the ImgwSynopStation representing the synoptic station to associate
     *                with the resulting WeatherForecast objects
     * @return a list of WeatherForecast objects containing the mapped weather data and
     *         associated synoptic station
     */
    default List<WeatherForecast> toSynopForecasts(OpenMeteoResponse dto, ImgwSynopStation station) {
        List<WeatherForecast> forecasts = mapCommonData(dto);
        forecasts.forEach(f -> f.setSynopStation(station));
        return forecasts;
    }

    /**
     * Converts an OpenMeteoResponse data transfer object into a list of virtual weather forecasts.
     *
     * @param dto the OpenMeteoResponse object containing weather data to be converted
     * @param station the VirtualStation object associated with the forecasts
     * @return a list of WeatherForecast objects linked to the given virtual station
     */
    default List<WeatherForecast> toVirtualForecasts(OpenMeteoResponse dto, VirtualStation station) {
        List<WeatherForecast> forecasts = mapCommonData(dto);
        forecasts.forEach(f -> f.setVirtualStation(station));
        return forecasts;
    }

    /**
     * Maps the data from an OpenMeteoResponse object to a list of WeatherForecast entities.
     * The method processes hourly and daily weather data, applying necessary transformations
     * and extracting relevant information including temperature, wind attributes, rain, UV index,
     * cloud cover, and weather codes.
     *
     * @param dto the OpenMeteoResponse object containing the raw weather data to be mapped
     * @return a list of WeatherForecast objects populated with the mapped data, or an empty list if no valid data is available
     */
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

            String timeStr = getSafe(hourly.getTime(), i);
            if (timeStr != null) {
                LocalDateTime forecastTime = LocalDateTime.parse(timeStr, ISO_FORMATTER);
                entity.setForecastTime(forecastTime);

                if (daily != null && daily.getTime() != null) {
                    int dayIndex = findDayIndex(daily.getTime(), forecastTime);
                    if (dayIndex != -1) {
                        entity.setSunrise(parseDateTime(getSafe(daily.getSunrise(), dayIndex)));
                        entity.setSunset(parseDateTime(getSafe(daily.getSunset(), dayIndex)));
                        entity.setUvIndexMax(toBigDecimal(getSafe(daily.getUvIndexMax(), dayIndex)));
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

    /**
     * Converts an {@code OpenMeteoLightResponse} DTO and a {@code VirtualStation} object
     * into a {@code VirtualStationData} entity.
     *
     * The method maps weather data from the given DTO to a newly created
     * {@code VirtualStationData} instance, linking it to the provided virtual station.
     * If the DTO or its current weather data is null, the method returns null.
     *
     * @param dto the {@code OpenMeteoLightResponse} object containing weather data to be converted
     * @param station the {@code VirtualStation} object associated with the resulting {@code VirtualStationData} entity
     * @return a {@code VirtualStationData} entity containing the mapped weather data and associated virtual station,
     *         or null if the input DTO or its current data is null
     */
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

    /**
     * Retrieves an element from the specified list at the given index in a safe manner.
     * Returns null if the list is null, the index is negative, or the index is out of bounds.
     *
     * @param list the list from which the element is to be retrieved
     * @param index the position of the element to retrieve
     * @param <T> the type of elements in the list
     * @return the element at the specified position or null if the conditions are not met
     */
    private <T> T getSafe(List<T> list, int index) {
        if (list != null && index >= 0 && index < list.size()) {
            return list.get(index);
        }
        return null;
    }

    /**
     * Converts a {@code Double} value to a {@code BigDecimal}.
     *
     * This method provides a safe conversion of a {@code Double} to a {@code BigDecimal}.
     * If the input {@code value} is null, the method will return null.
     *
     * @param value the {@code Double} value to convert
     * @return the converted {@code BigDecimal} value, or null if the input {@code value} is null
     */
    private BigDecimal toBigDecimal(Double value) {
        return value != null ? BigDecimal.valueOf(value) : null;
    }


    /**
     * Finds the index of a specific day in a list of days based on the given forecast time.
     * The method compares the forecast time's date with the strings in the list of days.
     *
     * @param days a list of string representations of days in the format "yyyy-MM-dd"
     * @param forecastTime the LocalDateTime object containing the date to be matched
     * @return the index of the matching day in the list; returns -1 if no match is found
     */
    private int findDayIndex(List<String> days, LocalDateTime forecastTime) {
        String targetDate = forecastTime.toLocalDate().toString();
        for (int i = 0; i < days.size(); i++) {
            if (targetDate.equals(days.get(i))) return i;
        }
        return -1;
    }

    /**
     * Parses a date-time string into a {@code LocalDateTime} object using a predefined formatter.
     * If the input string is null, the method returns null.
     *
     * @param dateTimeStr the string representation of the date-time to be parsed
     * @return a {@code LocalDateTime} object corresponding to the parsed input, or null if the input is null
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        return (dateTimeStr != null) ? LocalDateTime.parse(dateTimeStr, ISO_FORMATTER) : null;
    }
}