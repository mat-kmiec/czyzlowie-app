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

    public SunScheduleDto getSunScheduleForDate(Double lat, Double lon, LocalDate date) {
        double targetLat = (lat != null) ? lat : DEFAULT_LAT;
        double targetLon = (lon != null) ? lon : DEFAULT_LON;

        try {
            LocationFinderService.NearestStation nearest = locationFinderService.findNearestStation(
                    targetLat, targetLon, StationCategory.SYNOPTIC
            );

            log.info("Dla punktu [{}, {}] w dniu {} dopasowano stację słoneczną: {}",
                    targetLat, targetLon, date, nearest.stationId());

            Optional<MoonStationData> todayDataOpt = stationDataRepo.findById_StationIdAndId_CalculationDate(nearest.stationId(), date);
            Optional<MoonStationData> yesterdayDataOpt = stationDataRepo.findById_StationIdAndId_CalculationDate(nearest.stationId(), date.minusDays(1));

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

    private String calculateDayLengthDiff(long todaySec, long yesterdaySec) {
        long diffMin = (todaySec - yesterdaySec) / 60;
        if (diffMin == 0) return "bez zmian";
        return diffMin > 0 ? "+" + diffMin + " minut" : diffMin + " minut";
    }

    private String formatSecondsToHoursMinutes(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        return String.format("%dh %02dm", hours, minutes);
    }

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


    private record SunPhases(
            LocalDateTime nauticalDawn, LocalDateTime civilDawn, LocalDateTime sunrise,
            LocalDateTime morningGoldenEnd, LocalDateTime zenith, LocalDateTime eveningGoldenStart,
            LocalDateTime sunset, LocalDateTime civilDusk, LocalDateTime nauticalDusk
    ) {}

    private record DaylightStatus(boolean isDaylight, String timeToSunsetFmt) {}
}