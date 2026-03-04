package pl.czyzlowie.modules.fish_forecast.infrastructure.integration.synop;

import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.fish_forecast.domain.model.SynopSnapshot;
import pl.czyzlowie.modules.forecast.entity.VirtualStationData;
import pl.czyzlowie.modules.forecast.entity.WeatherForecast;
import pl.czyzlowie.modules.imgw_api.entity.ImgwSynopData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * A component that maps various sources of meteorological data into a unified SynopSnapshot format.
 * This class provides methods to process, transform, and convert data from different domain entities
 * such as ImgwSynopData, VirtualStationData, and WeatherForecast.
 *
 * The resulting SynopSnapshot provides a single standardized format for accessing synoptic weather data
 * with information such as temperature, wind speed, pressure, precipitation, and other weather attributes.
 * Additional processing is performed where needed, such as unit conversions or handling missing fields.
 */
@Component
public class SynopDataMapper {

    /**
     * Maps an instance of ImgwSynopData to a SynopSnapshot.
     * Converts and processes specific fields where necessary, including unit conversions
     * (e.g., wind speed from m/s to km/h) and timestamp adjustments.
     *
     * @param entity The source ImgwSynopData object containing raw meteorological data.
     * @return A SynopSnapshot object representing the mapped and processed weather data.
     */
    public SynopSnapshot fromImgw(ImgwSynopData entity) {
        BigDecimal windKmh = entity.getWindSpeed() != null
                ? BigDecimal.valueOf(entity.getWindSpeed()).multiply(BigDecimal.valueOf(3.6))
                : null;

        return SynopSnapshot.builder()
                .timestamp(truncateToHour(entity.getCreatedAt()))
                .temperature(entity.getTemperature())
                .pressure(entity.getPressure())
                .windSpeed(windKmh)
                .windDirection(entity.getWindDirection())
                .humidity(entity.getRelativeHumidity())
                .precipitation(entity.getTotalPrecipitation())
                .windGusts(null).cloudCover(null).apparentTemperature(null).uvIndex(null)
                .build();
    }

    /**
     * Maps an instance of VirtualStationData to a SynopSnapshot.
     * Copies and processes relevant fields from VirtualStationData to create
     * a unified representation of meteorological data.
     * Some fields (e.g., cloudCover, uvIndex) are not available in the
     * VirtualStationData source and are set to null.
     *
     * @param entity The source VirtualStationData object containing meteorological data.
     * @return A SynopSnapshot object representing the mapped weather data.
     */
    public SynopSnapshot fromVirtual(VirtualStationData entity) {
        return SynopSnapshot.builder()
                .timestamp(truncateToHour(entity.getMeasurementTime()))
                .temperature(entity.getTemperature())
                .pressure(entity.getPressure())
                .windSpeed(entity.getWindSpeed())
                .windDirection(entity.getWindDirection())
                .humidity(entity.getHumidity())
                .precipitation(entity.getRain())
                .windGusts(entity.getWindGusts())
                .apparentTemperature(entity.getApparentTemperature())
                .cloudCover(null)
                .uvIndex(null)
                .build();
    }

    /**
     * Maps an instance of WeatherForecast to a SynopSnapshot.
     * Extracts and processes specific fields from the WeatherForecast object to create
     * a unified representation of meteorological data.
     *
     * @param entity The WeatherForecast object containing forecasted meteorological data.
     * @return A SynopSnapshot object representing the mapped and processed weather data.
     */
    public SynopSnapshot fromForecast(WeatherForecast entity) {
        return SynopSnapshot.builder()
                .timestamp(truncateToHour(entity.getForecastTime()))
                .temperature(entity.getTemperature())
                .pressure(entity.getPressure())
                .windSpeed(entity.getWindSpeed())
                .windDirection(entity.getWindDirection())
                .humidity(BigDecimal.valueOf(entity.getRelativeHumidity2m()))
                .precipitation(entity.getRain())
                .windGusts(entity.getWindGusts())
                .apparentTemperature(entity.getApparentTemperature())
                .cloudCover(entity.getCloudCover())
                .uvIndex(entity.getUvIndex())
                .build();
    }

    /**
     * Truncates the given {@code LocalDateTime} to the hour precision by removing
     * all smaller units (minutes, seconds, and nanoseconds).
     * If the input is {@code null}, the method returns {@code null}.
     *
     * @param time The {@code LocalDateTime} object to be truncated. Can be {@code null}.
     * @return A {@code LocalDateTime} object truncated to the hour, or {@code null} if the input is {@code null}.
     */
    private LocalDateTime truncateToHour(LocalDateTime time) {
        if (time == null) return null;
        return time.truncatedTo(ChronoUnit.HOURS);
    }
}
