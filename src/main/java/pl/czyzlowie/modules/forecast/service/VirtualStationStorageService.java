package pl.czyzlowie.modules.forecast.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.forecast.entity.VirtualStationData;
import pl.czyzlowie.modules.forecast.repository.VirtualStationDataRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service responsible for managing the storage of virtual station data entries.
 * It ensures that only new, non-duplicate entries based on unique ID and measurement time are stored
 * into the database.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VirtualStationStorageService {

    private final VirtualStationDataRepository dataRepository;

    /**
     * Saves only new virtual station data entries that do not already exist in the database.
     * It compares the fetched data with the existing data in the database based on virtual station IDs
     * and measurement times. Only entries with unique combinations of ID and measurement time are persisted.
     *
     * @param fetchedData the list of virtual station data entries to be saved. Each entry must include
     *                    a non-null virtual station and measurement time. If the list is empty, the method exits early.
     */
    @Transactional
    public void saveNewDataOnly(List<VirtualStationData> fetchedData) {
        if (fetchedData.isEmpty()) return;

        Set<String> stationIds = fetchedData.stream()
                .map(d -> d.getVirtualStation().getId())
                .collect(Collectors.toSet());

        Set<LocalDateTime> times = fetchedData.stream()
                .map(VirtualStationData::getMeasurementTime)
                .collect(Collectors.toSet());

        List<VirtualStationData> existingData = dataRepository
                .findAllByVirtualStationIdInAndMeasurementTimeIn(stationIds, times);

        Set<String> existingKeys = existingData.stream()
                .map(this::generateUniqueKey)
                .collect(Collectors.toSet());

        List<VirtualStationData> toSave = fetchedData.stream()
                .filter(data -> !existingKeys.contains(generateUniqueKey(data)))
                .toList();

        if (!toSave.isEmpty()) {
            dataRepository.saveAll(toSave);
            log.info("Zapisano {} nowych pomiarów.", toSave.size());
        }
    }

    /**
     * Generates a unique key for a given virtual station data entry by combining the
     * virtual station ID and the measurement time.
     *
     * @param data the virtual station data entry for which the unique key is generated.
     *             It must include a non-null virtual station and measurement time.
     * @return a unique key in the format "virtualStationId|measurementTime".
     */
    private String generateUniqueKey(VirtualStationData data) {
        return data.getVirtualStation().getId() + "|" + data.getMeasurementTime();
    }
}