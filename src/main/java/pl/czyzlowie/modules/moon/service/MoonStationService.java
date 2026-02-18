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

/**
 * Service class responsible for generating moon station data, including calculations
 * related to sun and moon positions for a specific geographical location and date.
 */
@Service
public class MoonStationService {

    /**
     * Calculates and returns the moon station data based on the provided date, station details,
     * and geographical coordinates.
     *
     * @param date the specific date for which the calculation is performed
     * @param stationId a unique identifier for the station
     * @param stationType the type or category of the station
     * @param lat the latitude of the station in degrees
     * @param lon the longitude of the station in degrees
     * @return a MoonStationData object containing calculated sunrise, sunset, day length,
     *         moonrise, moonset, transit time, and maximum altitude
     */
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

    /**
     * Determines the time of the moon's transit (the point at which it reaches its
     * highest point in the sky) and calculates its maximum altitude for a specified
     * date and geographical location.
     *
     * @param date the date for which the transit time and maximum altitude are calculated
     * @param lat the latitude of the location in degrees
     * @param lon the longitude of the location in degrees
     * @param zone the time zone of the location
     * @return a TransitInfo object containing the transit time as a LocalDateTime and the
     *         maximum altitude as a BigDecimal
     */
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


    /**
     * Represents the moon's transit information, including the time at which the moon
     * reaches its highest point in the sky and the corresponding maximum altitude.
     *
     * This record is an immutable data structure that encapsulates:
     * - The transit time as a LocalDateTime.
     * - The maximum altitude of the moon during transit as a BigDecimal.
     *
     * Typically used to store computed transit details for a given geographical location
     * and date in the context of moon-related calculations.
     */
    private record TransitInfo(LocalDateTime time, BigDecimal maxAltitude) {}

    /**
     * Filters the input ZonedDateTime to return its LocalDateTime representation
     * if its date matches the specified target date.
     *
     * @param zdt the ZonedDateTime to be checked and converted; may be null
     * @param targetDate the target LocalDate to match against
     * @return the LocalDateTime representation of the input ZonedDateTime if the dates match;
     *         otherwise, null
     */
    private LocalDateTime filterToDate(ZonedDateTime zdt, LocalDate targetDate) {
        if (zdt == null) return null;
        if (zdt.toLocalDate().equals(targetDate)) {
            return zdt.toLocalDateTime();
        }
        return null;
    }
}