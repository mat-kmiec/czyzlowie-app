package pl.czyzlowie.modules.fish_forecast.infrastructure.integration.moon;

import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.fish_forecast.domain.model.MoonSnapshot;
import pl.czyzlowie.modules.moon.entity.MoonGlobalData;
import pl.czyzlowie.modules.moon.entity.MoonStationData;

@Component
public class MoonDataMapper {

    public MoonSnapshot toDomain(MoonGlobalData global, MoonStationData station) {
        return MoonSnapshot.builder()
                .date(global.getCalculationDate())
                .phaseName(global.getPhaseEnum().name())
                .illuminationPct(global.getIlluminationPct())
                .moonAgeDays(global.getMoonAgeDays())
                .isSuperMoon(global.getIsSuperMoon())

                .moonrise(station != null ? station.getMoonrise() : null)
                .moonset(station != null ? station.getMoonset() : null)
                .transit(station != null ? station.getTransit() : null)
                .sunrise(station != null ? station.getSunrise() : null)
                .sunset(station != null ? station.getSunset() : null)
                .build();
    }
}
