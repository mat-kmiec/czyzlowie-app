package pl.czyzlowie.modules.fish_forecast.infrastructure.integration.moon;

import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.fish_forecast.domain.model.MoonSnapshot;
import pl.czyzlowie.modules.moon.entity.MoonGlobalData;
import pl.czyzlowie.modules.moon.entity.MoonStationData;

/**
 * Responsible for mapping global lunar data and location-specific station data into
 * a consolidated MoonSnapshot domain object.
 *
 * This mapper bridges the gap between raw data sources, such as astronomical global
 * calculations and localized station-specific observations, to produce a rich,
 * unified representation of lunar and solar behavior for a given day.
 */
@Component
public class MoonDataMapper {

    /**
     * Maps the provided global and station data into a consolidated MoonSnapshot domain object.
     *
     * @param global The global lunar data containing information such as the calculation date,
     *               moon phase, illumination percentage, moon age, and supermoon indicator.
     * @param station The localized station data containing details such as the times for
     *                moonrise, moonset, transit, sunrise, and sunset. Can be null if station-specific
     *                data is not available.
     * @return A fully constructed MoonSnapshot object containing the combined information from
     *         the global and station data.
     */
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
