package pl.czyzlowie.modules.moon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.moon.entity.MoonStationData;
import pl.czyzlowie.modules.moon.entity.MoonStationDataId;
import pl.czyzlowie.modules.moon.projection.StationCoordinatesView;
import pl.czyzlowie.modules.moon.repository.MoonStationDataRepository;
import pl.czyzlowie.modules.imgw.repository.ImgwSynopStationRepository;
import pl.czyzlowie.modules.forecast.repository.VirtualStationRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoonStationBatchService {

    private final MoonStationService moonStationService;
    private final MoonStationDataRepository moonStationDataRepository;

    private final ImgwSynopStationRepository imgwSynopStationRepository;
    private final VirtualStationRepository virtualStationRepository;

    private record StationTask(LocalDate date, String stationId, String stationType, double lat, double lon) {}
    private record StationDef(String id, String type, double lat, double lon) {}

    @Transactional
    public void generateStationDataForDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Rozpoczynam weryfikację i generowanie lokalnych danych księżyca od {} do {}.", startDate, endDate);

        List<StationCoordinatesView> synopStations = imgwSynopStationRepository.findActiveStationCoordinates();
        List<StationCoordinatesView> virtualStations = virtualStationRepository.findActiveStationCoordinates();

        List<StationDef> allStations = new ArrayList<>();
        synopStations.forEach(s -> allStations.add(
                new StationDef(s.getId(), "SYNOP", s.getLatitude().doubleValue(), s.getLongitude().doubleValue())
        ));
        virtualStations.forEach(s -> allStations.add(
                new StationDef(s.getId(), "VIRTUAL", s.getLatitude().doubleValue(), s.getLongitude().doubleValue())
        ));

        if (allStations.isEmpty()) {
            log.warn("Brak aktywnych stacji w bazie danych. Przerywam generowanie danych księżyca.");
            return;
        }

        Set<MoonStationDataId> existingIds = moonStationDataRepository.findExistingIdsBetween(startDate, endDate);

        List<StationTask> tasksToCalculate = new ArrayList<>();
        List<LocalDate> datesInRange = startDate.datesUntil(endDate.plusDays(1)).toList();

        for (LocalDate date : datesInRange) {
            for (StationDef station : allStations) {
                MoonStationDataId uniqueId = new MoonStationDataId(station.id(), station.type(), date);

                if (!existingIds.contains(uniqueId)) {
                    tasksToCalculate.add(new StationTask(date, station.id(), station.type(), station.lat(), station.lon()));
                }
            }
        }

        if (tasksToCalculate.isEmpty()) {
            log.info("Wszystkie dane stacji dla podanego okresu już istnieją w bazie. Pomijam obliczenia.");
            return;
        }

        log.info("Do policzenia pozostało {} rekordów dla stacji. Uruchamiam obliczenia równoległe...", tasksToCalculate.size());

        List<MoonStationData> newDataToSave = tasksToCalculate.parallelStream()
                .map(task -> moonStationService.calculationStationData(
                        task.date(),
                        task.stationId(),
                        task.stationType(),
                        task.lat(),
                        task.lon()
                ))
                .toList();

        moonStationDataRepository.saveAll(newDataToSave);
        log.info("Zakończono sukcesem. Zapisano {} nowych rekordów stacji do bazy.", newDataToSave.size());
    }
}