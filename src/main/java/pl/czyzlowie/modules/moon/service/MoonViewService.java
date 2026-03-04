package pl.czyzlowie.modules.moon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.moon.dto.MoonDayDto;
import pl.czyzlowie.modules.moon.dto.SolunarActivity;
import pl.czyzlowie.modules.moon.entity.MoonGlobalData;
import pl.czyzlowie.modules.moon.entity.MoonStationData;
import pl.czyzlowie.modules.moon.repository.MoonGlobalDataRepository;
import pl.czyzlowie.modules.moon.repository.MoonStationDataRepository;
import pl.czyzlowie.modules.location.service.LocationFinderService;
import pl.czyzlowie.modules.location.enums.StationCategory;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing and retrieving moon-related data, including global moon data, station-specific data,
 * fishing day predictions, and calendar grids with lunar phases.
 * This service depends on various repositories and utilities to process and fetch the necessary data.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MoonViewService {

    private final MoonGlobalDataRepository globalDataRepo;
    private final MoonStationDataRepository stationDataRepo;
    private final SolunarCalculator solunarCalculator;
    private final LocationFinderService locationFinderService;
    private static final double DEFAULT_LAT = 52.2297;
    private static final double DEFAULT_LON = 21.0122;

    /**
     * Retrieves the global data for the moon based on the specified date.
     *
     * @param date the specific date for which the global moon data is to be retrieved
     * @return the global moon data for the specified date
     * @throws RuntimeException if no global data is found for the given date
     */
    public MoonGlobalData getGlobalDataForDate(LocalDate date) {
        return globalDataRepo.findByCalculationDate(date)
                .orElseThrow(() -> new RuntimeException("Brak danych globalnych dla daty: " + date + ". Sprawdź czy batch wygenerował dane."));
    }

    /**
     * Retrieves moon station data for the specified latitude, longitude, and date.
     *
     * @param lat the latitude for which the moon station data is to be retrieved; if null, a default latitude is used
     * @param lon the longitude for which the moon station data is to be retrieved; if null, a default longitude is used
     * @param date the date for which the moon station data is to be retrieved
     * @return the MoonStationData object containing relevant data for the specified parameters, or null if no data is found
     */
    public MoonStationData getStationDataForDate(Double lat, Double lon, LocalDate date) {
        double targetLat = (lat != null) ? lat : DEFAULT_LAT;
        double targetLon = (lon != null) ? lon : DEFAULT_LON;

        try {
            LocationFinderService.NearestStation nearest = locationFinderService.findNearestStation(
                    targetLat, targetLon, StationCategory.SYNOPTIC
            );

            log.info("Dla punktu [{}, {}] w dniu {} dopasowano stację księżycową: {}",
                    targetLat, targetLon, date, nearest.stationId());

            return stationDataRepo.findByIdStationIdAndIdCalculationDate(nearest.stationId(), date)
                    .orElse(null);

        } catch (Exception e) {
            log.warn("Błąd podczas wyszukiwania stacji dla parametrów księżycowych: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves the next excellent fishing day starting from the given date.
     *
     * This method searches for the best fishing day by analyzing future moon phase data within a
     * 30-day period starting from the day after the specified date. It uses the solunar activity
     * calculation to determine the days with "EXCELLENT" fishing activity.
     *
     * @param fromDate the date from which to start the search for the next excellent fishing day
     * @return the MoonGlobalData object representing the next excellent fishing day, or null if none is found
     */
    public MoonGlobalData getNextExcellentFishingDay(LocalDate fromDate) {
        List<MoonGlobalData> futureData = globalDataRepo.findByCalculationDateBetweenOrderByCalculationDateAsc(
                fromDate.plusDays(1), fromDate.plusDays(30)
        );

        return futureData.stream()
                .filter(d -> solunarCalculator.calculateActivity(d.getPhaseEnum()) == SolunarActivity.EXCELLENT)
                .findFirst()
                .orElse(null);
    }

    /**
     * Generates a calendar grid for a specific month, including details about lunar phases and other metadata.
     *
     * @param yearMonth the year and month for which the calendar should be generated
     * @return a list of MoonDayDto objects representing days in the specified month, including lunar phase details and placeholders for empty days
     */
    public List<MoonDayDto> getCalendarForMonth(YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<MoonGlobalData> monthData = globalDataRepo.findByCalculationDateBetweenOrderByCalculationDateAsc(startDate, endDate);
        List<MoonDayDto> calendarGrid = new ArrayList<>();

        int dayOfWeek = startDate.getDayOfWeek().getValue();
        for (int i = 1; i < dayOfWeek; i++) {
            calendarGrid.add(MoonDayDto.builder().isEmpty(true).build());
        }

        LocalDate today = LocalDate.now();
        for (MoonGlobalData data : monthData) {
            calendarGrid.add(MoonDayDto.builder()
                    .date(data.getCalculationDate())
                    .dayOfMonth(data.getCalculationDate().getDayOfMonth())
                    .phase(data.getPhaseEnum())
                    .phaseNamePl(data.getPhaseMoonPl())
                    .activity(solunarCalculator.calculateActivity(data.getPhaseEnum()))
                    .isToday(data.getCalculationDate().isEqual(today))
                    .isSuperMoon(data.getIsSuperMoon())
                    .isEmpty(false)
                    .build());
        }

        return calendarGrid;
    }
}