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

    public MoonGlobalData getGlobalDataForDate(LocalDate date) {
        return globalDataRepo.findByCalculationDate(date)
                .orElseThrow(() -> new RuntimeException("Brak danych globalnych dla daty: " + date + ". Sprawdź czy batch wygenerował dane."));
    }

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

    public MoonGlobalData getNextExcellentFishingDay(LocalDate fromDate) {
        List<MoonGlobalData> futureData = globalDataRepo.findByCalculationDateBetweenOrderByCalculationDateAsc(
                fromDate.plusDays(1), fromDate.plusDays(30)
        );

        return futureData.stream()
                .filter(d -> solunarCalculator.calculateActivity(d.getPhaseEnum()) == SolunarActivity.EXCELLENT)
                .findFirst()
                .orElse(null);
    }

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