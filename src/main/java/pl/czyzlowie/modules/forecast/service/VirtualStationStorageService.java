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

@Service
@RequiredArgsConstructor
@Slf4j
public class VirtualStationStorageService {

    private final VirtualStationDataRepository dataRepository;

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

    private String generateUniqueKey(VirtualStationData data) {
        return data.getVirtualStation().getId() + "|" + data.getMeasurementTime();
    }
}