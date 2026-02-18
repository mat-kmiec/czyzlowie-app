package pl.czyzlowie.modules.moon.service;

import org.shredzone.commons.suncalc.MoonPosition;
import org.shredzone.commons.suncalc.MoonTimes;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.moon.entity.MoonStationData;
import pl.czyzlowie.modules.moon.entity.MoonStationDataId;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

@Service
public class MoonStationService {

    public MoonStationData calculationStationData(LocalDate date, String stationId, String stationType, double lat, double lon) {
        MoonTimes times = MoonTimes.compute()
                .on(date)
                .at(lat, lon)
                .execute();

        TransitInfo transitInfo = findTransitAndMaxAltitude(date, lat, lon);
        MoonStationDataId id = new MoonStationDataId(stationId, stationType, date);

        return MoonStationData.builder()
                .id(id)
                .moonrise(toLocalDateTime(times.getRise()))
                .moonset(toLocalDateTime(times.getSet()))
                .transit(transitInfo.time)
                .maxAltitude(transitInfo.altitude)
                .build();
    }

    private LocalDateTime toLocalDateTime(ZonedDateTime zdt) {
        return zdt != null ? zdt.toLocalDateTime() : null;
    }

    private TransitInfo findTransitAndMaxAltitude(LocalDate date, double lat, double lon) {
        double maxAlt = -90.0;
        LocalDateTime bestTime = null;

        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 10) {
                LocalDateTime localTime = LocalDateTime.of(date, LocalTime.of(hour, minute));
                ZonedDateTime zdt = localTime.atZone(java.time.ZoneId.systemDefault());

                MoonPosition pos = MoonPosition.compute()
                        .on(zdt)
                        .at(lat, lon)
                        .execute();

                if (pos.getAltitude() > maxAlt) {
                    maxAlt = pos.getAltitude();
                    bestTime = localTime;
                }
            }
        }

        BigDecimal altBd = BigDecimal.valueOf(maxAlt).setScale(2, RoundingMode.HALF_UP);
        return new TransitInfo(bestTime, altBd);
    }


    private record  TransitInfo(LocalDateTime time, BigDecimal altitude){}
}
