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
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class MoonStationService {

    public MoonStationData calculationStationData(LocalDate date, String stationId, String stationType, double lat, double lon) {
        ZoneId zone = ZoneId.systemDefault();

        org.shredzone.commons.suncalc.SunTimes sunTimes = org.shredzone.commons.suncalc.SunTimes.compute()
                .on(date)
                .at(lat, lon)
                .timezone(zone)
                .execute();

        LocalDateTime sunrise = filterToDate(sunTimes.getRise(), date);
        LocalDateTime sunset = filterToDate(sunTimes.getSet(), date);

        Long dayLength = null;
        if (sunrise != null && sunset != null) {
            dayLength = java.time.Duration.between(sunrise, sunset).getSeconds();
        }

        MoonTimes moonTimes = MoonTimes.compute()
                .on(date)
                .at(lat, lon)
                .timezone(zone)
                .execute();

        LocalDateTime moonrise = filterToDate(moonTimes.getRise(), date);
        LocalDateTime moonset = filterToDate(moonTimes.getSet(), date);
        TransitInfo transitInfo = findTransitAndMaxAltitude(date, lat, lon, zone);

        return MoonStationData.builder()
                .id(new MoonStationDataId(stationId, stationType, date))
                .sunrise(sunrise)
                .sunset(sunset)
                .dayLengthSec(dayLength)
                .moonrise(moonrise)
                .moonset(moonset)
                .transit(transitInfo.time())
                .maxAltitude(transitInfo.maxAltitude())
                .build();
    }

    private TransitInfo findTransitAndMaxAltitude(LocalDate date, double lat, double lon, ZoneId zone) {
        double maxAlt = -90.0;
        ZonedDateTime bestHourTime = date.atStartOfDay(zone);

        for (int hour = 0; hour < 24; hour++) {
            ZonedDateTime time = date.atTime(hour, 0).atZone(zone);
            double alt = MoonPosition.compute().on(time).at(lat, lon).execute().getAltitude();
            if (alt > maxAlt) {
                maxAlt = alt;
                bestHourTime = time;
            }
        }

        maxAlt = -90.0;
        ZonedDateTime exactBestTime = bestHourTime;

        for (int i = -60; i <= 60; i++) {
            ZonedDateTime time = bestHourTime.plusMinutes(i);
            if (!time.toLocalDate().equals(date)) {
                continue;
            }

            double alt = MoonPosition.compute().on(time).at(lat, lon).execute().getAltitude();
            if (alt > maxAlt) {
                maxAlt = alt;
                exactBestTime = time;
            }
        }

        BigDecimal maxAltBd = BigDecimal.valueOf(maxAlt).setScale(2, RoundingMode.HALF_UP);
        return new TransitInfo(exactBestTime.toLocalDateTime(), maxAltBd);
    }


    private record TransitInfo(LocalDateTime time, BigDecimal maxAltitude) {}

    private LocalDateTime filterToDate(ZonedDateTime zdt, LocalDate targetDate) {
        if (zdt == null) return null;
        if (zdt.toLocalDate().equals(targetDate)) {
            return zdt.toLocalDateTime();
        }
        return null;
    }
}