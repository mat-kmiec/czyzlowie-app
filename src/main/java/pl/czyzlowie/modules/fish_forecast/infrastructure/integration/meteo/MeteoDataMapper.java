package pl.czyzlowie.modules.fish_forecast.infrastructure.integration.meteo;

import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.fish_forecast.domain.model.MeteoSnapshot;
import pl.czyzlowie.modules.imgw_api.entity.ImgwMeteoData;

@Component
public class MeteoDataMapper {

    public MeteoSnapshot toDomain(ImgwMeteoData entity) {
        return MeteoSnapshot.builder()
                .timestamp(entity.getCreatedAt())
                .airTemperature(entity.getAirTemp())
                .groundTemperature(entity.getGroundTemp())
                .windDirection(entity.getWindDirection())
                .windAverageSpeed(entity.getWindAvgSpeed())
                .windMaxSpeed(entity.getWindMaxSpeed())
                .windGust(entity.getWindGust10min())
                .humidity(entity.getRelativeHumidity())
                .precipitation10min(entity.getPrecipitation10min())
                .build();
    }
}
