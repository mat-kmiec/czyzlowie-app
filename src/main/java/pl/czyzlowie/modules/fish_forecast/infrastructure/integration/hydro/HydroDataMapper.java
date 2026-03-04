package pl.czyzlowie.modules.fish_forecast.infrastructure.integration.hydro;

import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.fish_forecast.domain.model.HydroSnapshot;
import pl.czyzlowie.modules.imgw_api.entity.ImgwHydroData;

/**
 * Converts an instance of ImgwHydroData entity into a domain model object HydroSnapshot.
 * This transformation bridges the gap between database-specific representation and
 * the domain layer, which is designed for application logic and business rules.
 *
 * The mapping extracts relevant hydrological data fields from the entity and puts
 * them into a unified snapshot format, which includes water level, water temperature,
 * discharge, ice phenomena, and vegetation overgrowth phenomena.
 *
 * This conversion ensures that the data is prepared in a way suitable for use in
 * domain-driven contexts, such as hydrological analysis, fish behavior predictions,
 * or other domain-specific logic.
 */
@Component
public class HydroDataMapper {

    /**
     * Maps the given ImgwHydroData entity to a HydroSnapshot domain object.
     *
     * @param entity the ImgwHydroData entity containing hydrological data to be transformed
     * @return a HydroSnapshot object representing the mapped domain data
     */
    public HydroSnapshot toDomain(ImgwHydroData entity) {
        return HydroSnapshot.builder()
                .timestamp(entity.getCreatedAt())
                .waterLevel(entity.getWaterLevel())
                .waterTemperature(entity.getWaterTemperature())
                .discharge(entity.getDischarge())
                .icePhenomenon(entity.getIcePhenomenon())
                .overgrowthPhenomenon(entity.getOvergrowthPhenomenon())
                .build();
    }

}
