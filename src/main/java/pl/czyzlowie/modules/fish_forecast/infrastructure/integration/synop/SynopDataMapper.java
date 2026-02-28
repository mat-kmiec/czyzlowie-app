package pl.czyzlowie.modules.fish_forecast.infrastructure.integration.synop;

import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.fish_forecast.domain.model.SynopSnapshot;
import pl.czyzlowie.modules.forecast.entity.VirtualStationData;
import pl.czyzlowie.modules.forecast.entity.WeatherForecast;
import pl.czyzlowie.modules.imgw_api.entity.ImgwSynopData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class SynopDataMapper {

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

    public SynopSnapshot fromForecast(WeatherForecast entity) {
        return SynopSnapshot.builder()
                .timestamp(truncateToHour(entity.getForecastTime()))
                .temperature(entity.getTemperature())
                .pressure(entity.getPressure())
                .windSpeed(entity.getWindSpeed())
                .windDirection(entity.getWindDirection())
                .humidity(null)
                .precipitation(entity.getRain())
                .windGusts(entity.getWindGusts())
                .apparentTemperature(entity.getApparentTemperature())
                .cloudCover(entity.getCloudCover())
                .uvIndex(entity.getUvIndex())
                .build();
    }

    private LocalDateTime truncateToHour(LocalDateTime time) {
        if (time == null) return null;
        return time.truncatedTo(ChronoUnit.HOURS);
    }
}
