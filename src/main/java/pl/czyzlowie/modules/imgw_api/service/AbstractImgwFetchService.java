package pl.czyzlowie.modules.imgw_api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An abstract service class that provides functionality for fetching and processing
 * data from an external API and synchronizing it with a database. The flow involves
 * fetching station data, mapping it to entities, and determining the need for updates
 * or new entries based on comparison with existing data.
 * This class is designed to be extended by a concrete implementation that provides
 * specific mappings and repository logic for the data and station entities.
 *
 * @param <DTO> The Data Transfer Object type received from the external API.
 * @param <S>   The station entity type in the database.
 * @param <D>   The data entity type in the database associated with stations.
 */
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



    /**
     * Fetches data from an external API and processes it by updating the database with new or updated
     * station and data records.
     * This method performs the following steps:
     * 1. Retrieves data from the external API. If no data is retrieved, logs a message and exits.
     * 2. Identifies station IDs from the retrieved data.
     * 3. Fetches existing stations from the database based on the retrieved station IDs.
     * 4. Identifies new stations that need to be created and adds them to the database.
     * 5. Retrieves the latest data for the affected stations from the database.
     * 6. For each data record, determines if it is newer than the currently recorded data in the database.
     * 7. Saves the new data in batch if there are any new records.
     * Logging is included at various stages of the process to provide traceability
     * and highlight significant actions, such as creating new stations and saving new data records.
     * The method is annotated with {@code @Transactional}, ensuring that all database operations
     * are executed within the same transaction, either entirely succeeding or entirely failing.
     */
    @Transactional
    public int fetchAndProcess() {
        List<DTO> dtos = fetchFromApi();
        if (dtos == null || dtos.isEmpty()) {
            log.info("Brak danych z API.");
            return 0;
        }

        Set<String> affectedStationIds = dtos.stream()
                .map(this::getStationIdFromDto)
                .collect(Collectors.toSet());

        Map<String, S> stationMap = getStationRepository().findAllById(affectedStationIds).stream()
                .collect(Collectors.toMap(this::getStationIdFromEntity, Function.identity()));

        List<S> newStationsToSave = new ArrayList<>();
        Set<String> processedNewStationIds = new HashSet<>();

        for (DTO dto : dtos) {
            String sId = getStationIdFromDto(dto);

            if (!stationMap.containsKey(sId) && !processedNewStationIds.contains(sId)) {
                S newStation = mapToStation(dto);
                newStationsToSave.add(newStation);
                processedNewStationIds.add(sId);
            }
        }

        if (!newStationsToSave.isEmpty()) {
            List<S> savedStations = getStationRepository().saveAll(newStationsToSave);
            log.info("Utworzono {} nowych stacji.", savedStations.size());

            for (S savedStation : savedStations) {
                stationMap.put(getStationIdFromEntity(savedStation), savedStation);
            }
        }

        Map<String, D> lastDataMap = getLatestDataMap(affectedStationIds);
        List<D> entitiesToSave = new ArrayList<>();

        for (DTO dto : dtos) {
            String sId = getStationIdFromDto(dto);
            S station = stationMap.get(sId);

            if (station == null) {
                log.error("Krytyczny błąd: Nie znaleziono stacji {} mimo próby zapisu.", sId);
                continue;
            }

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
            return entitiesToSave.size();
        } else {
            log.info("Brak nowych danych do zapisu.");
            return 0;
        }
    }
}