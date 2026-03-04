package pl.czyzlowie.modules.fish_forecast.infrastructure.integration.meteo;

import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.fish_forecast.domain.model.MeteoSnapshot;
import pl.czyzlowie.modules.imgw_api.entity.ImgwMeteoData;

/**
 * A component responsible for mapping meteorological data from the ImgwMeteoData entity to the domain model MeteoSnapshot.
 *
 * This mapper converts raw meteorological data fetched from the database into a structured domain representation
 * that can be used in the application's business logic, analytics, or forecasting modules.
 */
@Component
public class MeteoDataMapper {

    /**
     * Converts an instance of ImgwMeteoData into a MeteoSnapshot instance by mapping its fields.
     *
     * @param entity the source object of type ImgwMeteoData containing meteorological data to be transformed
     * @return a MeteoSnapshot object built using the data from the given ImgwMeteoData instance
     */
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
