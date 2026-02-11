package pl.czyzlowie.modules.moon.service;

import lombok.RequiredArgsConstructor;
import org.shredzone.commons.suncalc.MoonIllumination;
import org.shredzone.commons.suncalc.MoonPosition;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.moon.entity.Enums.MoonPhaseType;
import pl.czyzlowie.modules.moon.entity.MoonGlobalData;
import pl.czyzlowie.modules.moon.mapper.MoonPhaseMapper;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MoonGlobalService {

    private static final int SUPERMOON_DISTANCE = 360000;


    public MoonGlobalData calculateGlobalData(LocalDate date){
        MoonIllumination illumination = MoonIllumination.compute().on(date).execute();
        MoonPosition position = MoonPosition.compute().on(date).execute();
        MoonPhaseType phase = MoonPhaseMapper.mapToPhaseType(illumination);
        double phaseAngle = illumination.getPhase();
        double normalizedAngle = (phaseAngle < 0) ? phaseAngle + 360 : phaseAngle;
        double moonAge = (normalizedAngle / 360) * 29.53;
        double distance = position.getDistance();
        boolean isSuperMoon = (phase == MoonPhaseType.FULL_MOON) && (distance < SUPERMOON_DISTANCE);
        return MoonGlobalData.builder()
                .calculationDate(date)
                .phaseEnum(phase)
                .phaseMoonPl(phase.getName())
                .illuminationPct(toBigDecimal(illumination.getFraction() * 100))
                .moonAgeDays(toBigDecimal(moonAge))
                .isSuperMoon(isSuperMoon)
                .distanceKm((int) Math.round(distance))
                .build();
    }

    private BigDecimal toBigDecimal(double value) {
        return BigDecimal.valueOf(value).setScale(2, BigDecimal.ROUND_HALF_UP);
    }


}
