package pl.czyzlowie.modules.sun.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.location.enums.StationCategory;
import pl.czyzlowie.modules.location.service.LocationFinderService;
import pl.czyzlowie.modules.moon.entity.MoonStationData;
import pl.czyzlowie.modules.moon.repository.MoonStationDataRepository;
import pl.czyzlowie.modules.sun.dto.SunScheduleDto;
import pl.czyzlowie.modules.sun.dto.TimelineEventDto;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * SunViewService is a service class responsible for calculating and providing solar schedules
 * and phases based on location and date inputs. The class integrates with external repositories
 * and services to fetch necessary data and perform calculations to determine sun phases, timings,
 * and daylight-related information.
 *
 * It includes the following main functionalities:
 * - Fetching the nearest station based on provided geographic coordinates.
 * - Retrieving solar and astronomical data for specified dates from repositories.
 * - Calculating various solar phases such as sunrise, sunset, nautical twilight, civil twilight,
 *   and golden hours.
 * - Determining daylight status and calculating other time-based information such as day length
 *   differences, formatted timings, and fishing timelines.
 *
 * Notable constants:
 * - DEFAULT_LAT and DEFAULT_LON: Fallback geographical coordinates.
 * - NAUTICAL_TWILIGHT_MIN, CIVIL_TWILIGHT_MIN, etc.: Constants defining time ranges for various
 *   solar phases.
 * - TIME_FORMATTER: Defines the format used to represent time data.
 *
 * Error Handling:
 * - The service returns an empty data transfer object (SunScheduleDto) in case of missing or
 *   incomplete solar data.
 * - Exceptions during processing or data fetching are logged and handled gracefully to ensure
 *   service reliability.
 *
 * Dependencies:
 * - MoonStationDataRepository: Repository for fetching solar and station data.
 * - LocationFinderService: Service for determining the nearest weather station based on
 *   geographical data.
 * - SunScheduleDto and related classes such as DaylightStatus, SunPhases, and TimelineEventDto
 *   are used for encapsulating calculated solar information and timelines.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SunViewService {

    private final MoonStationDataRepository stationDataRepo;
    private final LocationFinderService locationFinderService;
    private static final double DEFAULT_LAT = 52.2297;
    private static final double DEFAULT_LON = 21.0122;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final int NAUTICAL_TWILIGHT_MIN = 75;
    private static final int CIVIL_TWILIGHT_MIN = 35;
    private static final int GOLDEN_HOUR_MIN = 60;
    private static final int ZENITH_WINDOW_MIN = 60;

    /**
     * Retrieves the sun schedule for a specific date and location.
     *
     * @param lat the latitude of the desired location. If null, a default latitude is used.
     * @param lon the longitude of the desired location. If null, a default longitude is used.
     * @param date the date for which the sun schedule is requested.
     * @return a SunScheduleDto object containing the sun schedule details. Returns an empty SunScheduleDto if data is unavailable or an error occurs.
     */
    public SunScheduleDto getSunScheduleForDate(Double lat, Double lon, LocalDate date) {
        double targetLat = (lat != null) ? lat : DEFAULT_LAT;
        double targetLon = (lon != null) ? lon : DEFAULT_LON;

        try {
            LocationFinderService.NearestStation nearest = locationFinderService.findNearestStation(
                    targetLat, targetLon, StationCategory.SYNOPTIC
            );

            log.info("Dla punktu [{}, {}] w dniu {} dopasowano stację słoneczną: {}",
                    targetLat, targetLon, date, nearest.stationId());

            Optional<MoonStationData> todayDataOpt = stationDataRepo.findByIdStationIdAndIdCalculationDate(nearest.stationId(), date);
            Optional<MoonStationData> yesterdayDataOpt = stationDataRepo.findByIdStationIdAndIdCalculationDate(nearest.stationId(), date.minusDays(1));

            if (todayDataOpt.isEmpty() || todayDataOpt.get().getSunrise() == null || todayDataOpt.get().getSunset() == null) {
                log.warn("Brak pełnych danych słonecznych dla stacji {} w dniu {}", nearest.stationId(), date);
                return SunScheduleDto.builder().build(); // Puste DTO
            }

            return calculateSunSchedule(todayDataOpt.get(), yesterdayDataOpt.orElse(null), date);

        } catch (Exception e) {
            log.warn("Błąd podczas generowania harmonogramu słonecznego: {}", e.getMessage(), e);
            return SunScheduleDto.builder().build();
        }
    }

    /**
     * Calculates the sun schedule for the given date based on provided moon station data and
     * returns a DTO containing sun phase timings, day length information, and related metadata.
     *
     * @param today the MoonStationData object containing sunrise, sunset, and day length information for the selected date
     * @param yesterday the MoonStationData object containing sunrise, sunset, and day length information for the previous date
     * @param selectedDate the LocalDate representing the selected date for which the sun schedule is to be calculated
     * @return a SunScheduleDto object containing computed sunrise, sunset, zenith, various dawn and dusk timings,
     *         formatted day length, daylight status, and additional information for the selected date
     */
    private SunScheduleDto calculateSunSchedule(MoonStationData today, MoonStationData yesterday, LocalDate selectedDate) {
        SunPhases phases = calculatePhases(today.getSunrise(), today.getSunset());
        long todayDayLength = today.getDayLengthSec() != null ? today.getDayLengthSec() : Duration.between(phases.sunrise(), phases.sunset()).getSeconds();
        long yesterdayDayLength = (yesterday != null && yesterday.getDayLengthSec() != null) ? yesterday.getDayLengthSec() : todayDayLength;
        boolean isToday = selectedDate.isEqual(LocalDate.now());
        DaylightStatus daylightStatus = checkDaylightStatus(isToday, phases.sunrise(), phases.sunset());

        return SunScheduleDto.builder()
                .sunrise(phases.sunrise())
                .sunset(phases.sunset())
                .zenith(phases.zenith())
                .nauticalDawn(phases.nauticalDawn())
                .civilDawn(phases.civilDawn())
                .civilDusk(phases.civilDusk())
                .nauticalDusk(phases.nauticalDusk())
                .morningGoldenEnd(phases.morningGoldenEnd())
                .eveningGoldenStart(phases.eveningGoldenStart())
                .sunriseTime(phases.sunrise().format(TIME_FORMATTER))
                .sunsetTime(phases.sunset().format(TIME_FORMATTER))
                .dayLengthFormatted(formatSecondsToHoursMinutes(todayDayLength))
                .dayLengthDifferenceFormatted(calculateDayLengthDiff(todayDayLength, yesterdayDayLength) + " w stosunku do wczoraj")
                .timeToSunsetFormatted(daylightStatus.timeToSunsetFmt())
                .isDaylight(daylightStatus.isDaylight())
                .isToday(isToday)
                .timeline(buildFishingTimeline(phases))
                .build();
    }

    /**
     * Calculates the different phases of the sun based on the given sunrise and sunset times.
     *
     * @param sunrise the LocalDateTime representing the sunrise time
     * @param sunset the LocalDateTime representing the sunset time
     * @return a SunPhases object containing the calculated phases such as nautical dawn, civil dawn, sunrise,
     *         morning golden end, zenith, evening golden start, sunset, civil dusk, and nautical dusk
     */
    private SunPhases calculatePhases(LocalDateTime sunrise, LocalDateTime sunset) {
        long halfDaySec = Duration.between(sunrise, sunset).getSeconds() / 2;
        LocalDateTime zenith = sunrise.plusSeconds(halfDaySec);

        return new SunPhases(
                sunrise.minusMinutes(NAUTICAL_TWILIGHT_MIN), // nauticalDawn
                sunrise.minusMinutes(CIVIL_TWILIGHT_MIN),    // civilDawn
                sunrise,                                     // sunrise
                sunrise.plusMinutes(GOLDEN_HOUR_MIN),        // morningGoldenEnd
                zenith,                                      // zenith
                sunset.minusMinutes(GOLDEN_HOUR_MIN),        // eveningGoldenStart
                sunset,                                      // sunset
                sunset.plusMinutes(CIVIL_TWILIGHT_MIN),      // civilDusk
                sunset.plusMinutes(NAUTICAL_TWILIGHT_MIN)    // nauticalDusk
        );
    }

    /**
     * Determines the daylight status based on the current time in relation to the provided sunrise and sunset times.
     *
     * @param isToday a boolean indicating whether the day being checked is today;
     *                if false, the method assumes there is no daylight.
     * @param sunrise the LocalDateTime representing the sunrise time.
     * @param sunset the LocalDateTime representing the sunset time.
     * @return a DaylightStatus object indicating whether it is currently daylight and,
     *         if so, the formatted time remaining until sunset; otherwise, indicates no daylight.
     */
    private DaylightStatus checkDaylightStatus(boolean isToday, LocalDateTime sunrise, LocalDateTime sunset) {
        if (!isToday) {
            return new DaylightStatus(false, "");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(sunrise) && now.isBefore(sunset)) {
            Duration untilSunset = Duration.between(now, sunset);
            String timeFmt = String.format("%dh %02dm", untilSunset.toHours(), untilSunset.toMinutesPart());
            return new DaylightStatus(true, timeFmt);
        }
        return new DaylightStatus(false, "");
    }

    /**
     * Calculates the difference in day length between today and yesterday in minutes.
     *
     * @param todaySec the duration of daylight today in seconds
     * @param yesterdaySec the duration of daylight yesterday in seconds
     * @return a string representing the difference in minutes, with a "+" sign for positive differences,
     *         "bez zmian" if there is no difference, or a negative value if yesterday was longer
     */
    private String calculateDayLengthDiff(long todaySec, long yesterdaySec) {
        long diffMin = (todaySec - yesterdaySec) / 60;
        if (diffMin == 0) return "bez zmian";
        return diffMin > 0 ? "+" + diffMin + " minut" : diffMin + " minut";
    }

    /**
     * Converts the given total seconds into a formatted string representing
     * hours and minutes in the format "Xh YYm".
     *
     * @param totalSeconds the total number of seconds to be converted
     *                     into hours and minutes.
     * @return a formatted string in the form "Xh YYm" where X is the number of hours
     *         and YY is the number of minutes.
     */
    private String formatSecondsToHoursMinutes(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        return String.format("%dh %02dm", hours, minutes);
    }

    /**
     * Builds a list of timeline events representing various fishing activity periods based on the given sun phases.
     *
     * @param p an object representing the phases of the sun, including key times such as nautical dawn, sunrise, and sunset
     * @return a list of {@code TimelineEventDto} objects, each representing a specific time period and its corresponding fishing activity details
     */
    private List<TimelineEventDto> buildFishingTimeline(SunPhases p) {
        return List.of(
                createTimelineEvent(p.nauticalDawn(), p.civilDawn(), "Świt Żeglarski (Koniec Nocy)",
                        "Wciąż ciemno. <strong>Grube Leszcze i Karpie</strong> kończą nocne odkurzanie najpłytszych blatów. <strong>Sandacze i Sumy</strong> wciąż żerują przy brzegu.",
                        "moon", "text-indigo-400", null, null, false),

                createTimelineEvent(p.civilDawn(), p.sunrise(), "Świt Cywilny (Szarówka)",
                        "Pojawiają się kolory. W grążelach budzą się <strong>Liny i Karasie</strong>. Drobnica zaczyna ruch, co prowokuje agresywne ataki <strong>Boleni i Okoni</strong>.",
                        "sunrise", "text-blue-400", null, null, false),

                createTimelineEvent(p.sunrise(), p.morningGoldenEnd(), "Poranna Złota Godzina",
                        "Potężny szczyt aktywności <strong>Szczupaka</strong>, który wykorzystuje półmrok pod wodą. Dla białorybu to czas intensywnego poszukiwania owadów.",
                        "sun", null, "bg-sun-gold", "text-sun-gold text-glow-gold", true),

                createTimelineEvent(p.zenith().minusMinutes(ZENITH_WINDOW_MIN), p.zenith().plusMinutes(ZENITH_WINDOW_MIN), "Górowanie (Wysokie Słońce)",
                        "Większość ryb ucieka w głębiny. Prime-time na ciepłolubnego <strong>Amura i Klenia</strong>. Wiosną i jesienią <strong>Karpie</strong> wygrzewają się na płyciznach.",
                        "sun-dim", "text-white opacity-50", null, null, false),

                createTimelineEvent(p.eveningGoldenStart(), p.sunset(), "Wieczorna Złota Godzina",
                        "Zgrupowania drobnicy w trzcinach wywołują rzeź ze strony <strong>Szczupaków i Boleni</strong>. Gruby białoryb wychodzi z ukrycia. Woda tętni życiem.",
                        "sunset", null, "bg-sun-orange", "text-sun-orange text-glow-orange", true),

                createTimelineEvent(p.sunset(), p.civilDusk(), "Zmierzch Cywilny (Zapadanie mroku)",
                        "Spławiki ledwo widoczne. Drapieżniki dzienne chowają się, a <strong>Karpie i Leszcze</strong> ruszają na żerowiska nęcone za dnia. Ostrożność ryb spada.",
                        "moon-star", "text-indigo-300", null, null, false),

                createTimelineEvent(p.civilDusk(), p.nauticalDusk(), "Zmierzch Żeglarski (Czas Wampirów)",
                        "Robi się ciemno. <strong>Sandacze i Sumy</strong> wchodzą na śmiesznie płytką wodę (0.5 - 2m). Łowcy <strong>Karpi</strong> notują pierwsze agresywne 'odjazdy'.",
                        "moon", "text-brand-green", null, null, false)
        );
    }

    /**
     * Creates a timeline event based on the provided parameters and returns a constructed TimelineEventDto object.
     *
     * @param start the start time of the timeline event
     * @param end the end time of the timeline event
     * @param title the title of the timeline event
     * @param desc the description of the timeline event
     * @param icon the icon associated with the timeline event
     * @param iconColor the CSS class for the icon color
     * @param iconBg the CSS class for the icon background
     * @param titleClass the CSS class for the title styling
     * @param isGolden flag indicating whether the event is marked as a "golden hour" event
     * @return a TimelineEventDto object representing the timeline event
     */
    private TimelineEventDto createTimelineEvent(LocalDateTime start, LocalDateTime end, String title, String desc, String icon, String iconColor, String iconBg, String titleClass, boolean isGolden) {
        return TimelineEventDto.builder()
                .timeRange(start.format(TIME_FORMATTER) + " - " + end.format(TIME_FORMATTER))
                .title(title)
                .description(desc)
                .icon(icon)
                .iconColorClass(iconColor)
                .iconBgClass(iconBg)
                .titleClass(titleClass)
                .isGoldenHour(isGolden)
                .build();
    }


    /**
     * Represents the different phases of the Sun during a specific day.
     * This record captures various key times from dawn to dusk including golden hour periods.
     *
     * This class is immutable and thread-safe.
     *
     * Fields:
     * - nauticalDawn: The time of the first appearance of light before sunrise when the sun is 12 degrees below the horizon.
     * - civilDawn: The time of the first noticeable light before sunrise when the sun is 6 degrees below the horizon.
     * - sunrise: The time when the top edge of the Sun appears over the horizon.
     * - morningGoldenEnd: The time when the morning golden hour ends.
     * - zenith: The time when the sun reaches its highest point in the sky for the day.
     * - eveningGoldenStart: The time when the evening golden hour begins.
     * - sunset: The time when the top edge of the Sun disappears below the horizon.
     * - civilDusk: The time after sunset when the Sun is 6 degrees below the horizon.
     * - nauticalDusk: The time after sunset when the Sun is 12 degrees below the horizon.
     */
    private record SunPhases(
            LocalDateTime nauticalDawn, LocalDateTime civilDawn, LocalDateTime sunrise,
            LocalDateTime morningGoldenEnd, LocalDateTime zenith, LocalDateTime eveningGoldenStart,
            LocalDateTime sunset, LocalDateTime civilDusk, LocalDateTime nauticalDusk
    ) {}

    /**
     * A record representing the daylight status at a specific moment.
     *
     * This record holds information about whether it is currently daylight and
     * provides a formatted string*/
    private record DaylightStatus(boolean isDaylight, String timeToSunsetFmt) {}
}