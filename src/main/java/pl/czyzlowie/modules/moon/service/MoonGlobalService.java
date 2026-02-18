package pl.czyzlowie.modules.moon.service;

import lombok.RequiredArgsConstructor;
import org.shredzone.commons.suncalc.MoonIllumination;
import org.shredzone.commons.suncalc.MoonPosition;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.moon.entity.Enums.MoonPhaseType;
import pl.czyzlowie.modules.moon.entity.MoonGlobalData;
import pl.czyzlowie.modules.moon.mapper.MoonPhaseMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * The MoonGlobalService class provides functionalities to calculate and retrieve
 * various global data related to the moon for a given date. This includes
 * determining the moon's phase, illumination, age, distance, and whether it
 * qualifies as a supermoon.
 *
 * This service uses astronomical calculations for moon positions and illumination
 * to compute precise values. The methods leverage the provided external libraries
 * and mappers in the system to ensure accurate and meaningful results.
 */
@Service
@RequiredArgsConstructor
public class MoonGlobalService {

    private static final int SUPERMOON_DISTANCE = 362000;


    /**
     * Calculates and returns global moon data for a given date, including moon phase,
     * illumination percentage, age, distance, and supermoon classification.
     *
     * @param date the date for which the moon data is to be calculated
     * @return an instance of {@code MoonGlobalData} containing the computed moon data
     */
    public MoonGlobalData calculateGlobalData(LocalDate date){
        MoonIllumination illuminationToday = MoonIllumination.compute().on(date).execute();
        MoonIllumination illuminationTomorrow = MoonIllumination.compute().on(date.plusDays(1)).execute();

        MoonPosition position = MoonPosition.compute()
                .on(date)
                .at(52.06, 19.25) // Center of Poland
                .execute();

        double angleToday = illuminationToday.getPhase();
        double angleTomorrow = illuminationTomorrow.getPhase();
        MoonPhaseType phase = MoonPhaseMapper.calculateDailyPhase(angleToday, angleTomorrow);
        double normalizedAngle = (angleToday < 0) ? angleToday + 360 : angleToday;
        double moonAge = ((angleToday + 180.0) / 360.0) * 29.53;
        double distance = position.getDistance();
        boolean isSuperMoon = (phase == MoonPhaseType.FULL_MOON) && (distance < SUPERMOON_DISTANCE);
        return MoonGlobalData.builder()
                .calculationDate(date)
                .phaseEnum(phase)
                .phaseMoonPl(phase.getName())
                .illuminationPct(toBigDecimal(illuminationToday.getFraction() * 100))
                .moonAgeDays(toBigDecimal(moonAge))
                .isSuperMoon(isSuperMoon)
                .distanceKm((int) Math.round(distance))
                .build();
    }

    /**
     * Converts a double value to a {@code BigDecimal} with a scale of 2 and rounding mode set to HALF_UP.
     *
     * @param value the double value to be converted
     * @return the {@code BigDecimal} representation of the input value with a scale of 2
     */
    private BigDecimal toBigDecimal(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }


}
