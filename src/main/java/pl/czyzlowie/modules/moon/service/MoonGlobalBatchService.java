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

@Slf4j
@Service
@RequiredArgsConstructor
public class MoonGlobalBatchService {

    private final MoonGlobalService moonGlobalService;
    private final MoonGlobalDataRepository moonGlobalDataRepository;


    @Transactional
    public void generateGlobalDataForYear(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        generateGlobalDataForDateRange(startDate, endDate);
    }

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
