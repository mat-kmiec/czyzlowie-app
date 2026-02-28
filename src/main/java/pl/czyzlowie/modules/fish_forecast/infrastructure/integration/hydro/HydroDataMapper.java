package pl.czyzlowie.modules.fish_forecast.infrastructure.integration.hydro;

import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.fish_forecast.domain.model.HydroSnapshot;
import pl.czyzlowie.modules.imgw_api.entity.ImgwHydroData;

@Component
public class HydroDataMapper {

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
