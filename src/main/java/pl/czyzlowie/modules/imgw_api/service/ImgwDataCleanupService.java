package pl.czyzlowie.modules.imgw_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.imgw_api.entity.ImgwSynopData;
import pl.czyzlowie.modules.imgw_api.repository.ImgwHydroDataRepository;
import pl.czyzlowie.modules.imgw_api.repository.ImgwMeteoDataRepository;
import pl.czyzlowie.modules.imgw_api.repository.ImgwSynopDataRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImgwDataCleanupService {

    private final ImgwMeteoDataRepository meteoRepo;
    private final ImgwSynopDataRepository synopRepo;
    private final ImgwHydroDataRepository hydroRepo;

    public void cleanupOldData(int daysToKeep) {
        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(daysToKeep);

        log.info("Rozpoczynam sekwencyjne czyszczenie danych IMGW starszych niż {}", thresholdDate);

        int meteoDeleted = meteoRepo.deleteOlderThan(thresholdDate);
        log.info("Usunięto {} starych rekordów Meteo.", meteoDeleted);

        int synopDeleted = synopRepo.deleteOlderThan(thresholdDate);
        log.info("Usunięto {} starych rekordów Synop.", synopDeleted);

        int hydroDeleted = hydroRepo.deleteOlderThan(thresholdDate);
        log.info("Usunięto {} starych rekordów Hydro.", hydroDeleted);

        log.info("Nocne czyszczenie bazy zakończone sukcesem.");
    }
}
