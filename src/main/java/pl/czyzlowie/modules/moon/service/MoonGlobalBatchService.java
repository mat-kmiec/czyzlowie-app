package pl.czyzlowie.modules.moon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.moon.entity.MoonGlobalData;
import pl.czyzlowie.modules.moon.repository.MoonGlobalDataRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Service responsible for managing and processing global lunar data batch operations.
 * It provides methods to generate and store lunar data for specified years or date ranges.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MoonGlobalBatchService {

    private final MoonGlobalService moonGlobalService;
    private final MoonGlobalDataRepository moonGlobalDataRepository;


    /**
     * Generates global lunar data for the specified year by processing and saving
     * data for all dates within the specified year's range. The method determines
     * the date range for the given year and delegates the operation to another method
     * for processing the data within that range.
     *
     * @param year the year for which global lunar data should be generated
     */
    @Transactional
    public void generateGlobalDataForYear(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        generateGlobalDataForDateRange(startDate, endDate);
    }

    /**
     * Generates global lunar data for a given date range. It identifies dates within the range
     * that do not already have associated data in the database, calculates the missing data,
     * and stores it in the database.
     *
     * @param startDate the starting date of the range (inclusive).
     * @param endDate the ending date of the range (inclusive).
     */
    @Transactional
    public void generateGlobalDataForDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Rozpoczynam weryfikację i generowanie globalnych danych księżyca od {} do {}.", startDate, endDate);

        List<LocalDate> allDatesInRange = startDate.datesUntil(endDate.plusDays(1)).toList();
        Set<LocalDate> existingDates = moonGlobalDataRepository.findExistingDatesBetween(startDate, endDate);

        List<LocalDate> datesToCalculate = allDatesInRange.stream()
                .filter(date -> !existingDates.contains(date))
                .toList();

        if (datesToCalculate.isEmpty()) {
            log.info("Wszystkie dane dla podanego okresu ({} - {}) już istnieją w bazie. SKIP.", startDate, endDate);
            return;
        }

        log.info("Do policzenia pozostało {} dni. Rozpoczynanie obliczenia równoległego...", datesToCalculate.size());

        List<MoonGlobalData> newDataToSave = datesToCalculate.parallelStream()
                .map(moonGlobalService::calculateGlobalData)
                .toList();

        moonGlobalDataRepository.saveAll(newDataToSave);
        log.info("Zakończono sukcesem. Zapisano {} nowych rekordów do bazy.", newDataToSave.size());
    }
}
