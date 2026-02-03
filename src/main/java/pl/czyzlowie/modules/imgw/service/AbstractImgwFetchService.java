package pl.czyzlowie.modules.imgw.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractImgwFetchService<DTO, S, D> {

    protected abstract List<DTO> fetchFromApi();
    protected abstract String getStationIdFromDto(DTO dto);
    protected abstract String getStationIdFromEntity(S station);
    protected abstract S mapToStation(DTO dto);
    protected abstract D mapToData(DTO dto);
    protected abstract void setStationToData(D data, S station);
    protected abstract JpaRepository<S, String> getStationRepository();
    protected abstract JpaRepository<D, Long> getDataRepository();
    protected abstract Map<String, D> getLatestDataMap(Set<String> stationIds);
    protected abstract boolean isNewer(D lastKnownData, D newData);


    @Transactional
    public void fetchAndProcess() {
        List<DTO> dtos = fetchFromApi();
        if (dtos == null || dtos.isEmpty()) {
            log.info("Brak danych z API.");
            return;
        }

        Set<String> affectedStationIds = dtos.stream()
                .map(this::getStationIdFromDto)
                .collect(Collectors.toSet());

        Map<String, S> stationMap = getStationRepository().findAllById(affectedStationIds).stream()
                .collect(Collectors.toMap(this::getStationIdFromEntity, Function.identity()));

        List<S> newStationsToSave = new ArrayList<>();
        for (DTO dto : dtos) {
            String sId = getStationIdFromDto(dto);
            if (!stationMap.containsKey(sId)) {
                S newStation = mapToStation(dto);
                newStationsToSave.add(newStation);
                stationMap.put(sId, newStation);
            }
        }

        if (!newStationsToSave.isEmpty()) {
            getStationRepository().saveAll(newStationsToSave);
            log.info("Utworzono {} nowych stacji.", newStationsToSave.size());
        }

        Map<String, D> lastDataMap = getLatestDataMap(affectedStationIds);
        List<D> entitiesToSave = new ArrayList<>();

        for (DTO dto : dtos) {
            String sId = getStationIdFromDto(dto);
            S station = stationMap.get(sId);

            D newData = mapToData(dto);
            D lastData = lastDataMap.get(sId);

            if (lastData == null || isNewer(lastData, newData)) {
                setStationToData(newData, station);
                entitiesToSave.add(newData);
            }
        }

        if (!entitiesToSave.isEmpty()) {
            getDataRepository().saveAll(entitiesToSave);
            log.info("Zapisano {} nowych rekordów (Batch).", entitiesToSave.size());
        } else {
            log.info("Brak nowych danych do zapisu.");
        }
    }
}