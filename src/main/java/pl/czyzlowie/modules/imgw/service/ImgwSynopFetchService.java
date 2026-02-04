package pl.czyzlowie.modules.imgw.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.imgw.client.ImgwClient;
import pl.czyzlowie.modules.imgw.client.dto.ImgwSynopResponseDto;
import pl.czyzlowie.modules.imgw.config.ImgwApiProperties;
import pl.czyzlowie.modules.imgw.entity.ImgwSynopData;
import pl.czyzlowie.modules.imgw.entity.ImgwSynopStation;
import pl.czyzlowie.modules.imgw.mapper.ImgwSynopMapper;
import pl.czyzlowie.modules.imgw.repository.ImgwSynopDataRepository;
import pl.czyzlowie.modules.imgw.repository.ImgwSynopStationRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A service responsible for fetching and processing synoptic data from the IMGW API.
 * This service extends the {@link AbstractImgwFetchService} to handle operations specific
 * to synoptic data and stations, including API data fetching, mapping, and repository interactions.
 *
 * The service utilizes the following components:
 * - {@link ImgwClient} for API communication.
 * - {@link ImgwApiProperties} for accessing configuration related to API URLs.
 * - {@link ImgwSynopStationRepository} for CRUD operations on synoptic weather stations.
 * - {@link ImgwSynopDataRepository} for CRUD operations on synoptic weather data.
 * - {@link ImgwSynopMapper} to map between DTOs and entities.
 *
 * Overrides abstract methods to implement functionality specific to synoptic data fetching,
 * including determining whether new data is more recent, mapping API responses to entities,
 * and persistently storing the resulting objects.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImgwSynopFetchService extends AbstractImgwFetchService<ImgwSynopResponseDto, ImgwSynopStation, ImgwSynopData> {

    private final ImgwClient imgwClient;
    private final ImgwApiProperties properties;
    private final ImgwSynopStationRepository stationRepo;
    private final ImgwSynopDataRepository dataRepo;
    private final ImgwSynopMapper mapper;

    /**
     * Fetches a list of synoptic data from the IMGW API.
     *
     * This method communicates with the IMGW API using the configured client
     * and retrieves a list of synoptic data DTOs based on the provided API URL.
     *
     * @return a list of {@link ImgwSynopResponseDto} objects representing the fetched synoptic data,
     *         or an empty list if the API call fails or returns no data.
     */
    @Override
    protected List<ImgwSynopResponseDto> fetchFromApi() {
        return imgwClient.fetchList(
                properties.getSynopUrl(),
                new ParameterizedTypeReference<>() {}
        );
    }

    /**
     * Retrieves the latest synoptic data for the given set of station IDs and maps it to a dictionary
     * where the key is the station ID and the value is the corresponding synoptic data entity.
     *
     * The method queries the database to fetch the most recent data for each station in the provided set.
     *
     * @param stationIds a set of station IDs for which the latest synoptic data should be retrieved
     * @return a map where each key is a station ID and each value is the latest {@link ImgwSynopData}
     *         object corresponding to that station
     */
    @Override
    protected Map<String, ImgwSynopData> getLatestDataMap(Set<String> stationIds) {
        return dataRepo.findLatestDataForStations(stationIds).stream()
                .collect(Collectors.toMap(
                        d -> d.getStation().getId(),
                        Function.identity()
                ));
    }

    /**
     * Determines whether the new synoptic data is more recent compared to the last known data.
     *
     * This method compares the measurement date and hour of the new data
     * with those of the last known data to establish if the new data represents
     * a more recent entry. If the last known data is null, the new data is considered newer.
     *
     * @param lastKnownData the previously stored synoptic data, which can be null
     * @param newData       the new synoptic data to be evaluated
     * @return true if the new data is considered more recent, false otherwise
     */
    @Override
    protected boolean isNewer(ImgwSynopData lastKnownData, ImgwSynopData newData) {
        if (lastKnownData == null) return true;

        boolean dateChanged = !newData.getMeasurementDate().equals(lastKnownData.getMeasurementDate());
        boolean hourChanged = !newData.getMeasurementHour().equals(lastKnownData.getMeasurementHour());

        return dateChanged || hourChanged;
    }

    /**
     * Extracts the station ID from the given DTO.
     *
     * @param dto the {@link ImgwSynopResponseDto} object from which the station ID is extracted
     * @return the station ID as a String
     */
    @Override protected String getStationIdFromDto(ImgwSynopResponseDto dto) { return dto.getStationId(); }

    /**
     * Extracts the station ID from the given entity.
     *
     * This method retrieves the unique identifier of the station from the provided
     * {@link ImgwSynopStation} entity.
     *
     * @param station the {@link ImgwSynopStation} entity from which the station ID is extracted
     * @return the station ID as a String
     */
    @Override protected String getStationIdFromEntity(ImgwSynopStation station) { return station.getId(); }

    /**
     * Maps the provided DTO object to an instance of {@link ImgwSynopStation}.
     *
     * This method utilizes the mapper to transform the data transfer object into
     * a corresponding entity representation.
     *
     * @param dto the {@link ImgwSynopResponseDto} object containing the data to be mapped
     * @return an instance of {@link ImgwSynopStation} representing the mapped data
     */
    @Override protected ImgwSynopStation mapToStation(ImgwSynopResponseDto dto) { return mapper.toSynopStation(dto); }

    /**
     * Maps the provided DTO object to an instance of {@link ImgwSynopData}.
     *
     * This method uses the {@link ImgwSynopMapper} to transform the data transfer object
     * into a corresponding entity representation.
     *
     * @param dto the {@link ImgwSynopResponseDto} object containing the data to be mapped
     * @return an instance of {@link ImgwSynopData} representing the mapped data
     */
    @Override protected ImgwSynopData mapToData(ImgwSynopResponseDto dto) { return mapper.toSynopData(dto); }

    /**
     * Sets the station information for the given synoptic data entity.
     *
     * This method associates the specified {@link ImgwSynopStation} with the provided
     * {@link ImgwSynopData} entity by assigning the station to the data object.
     *
     * @param data    the {@link ImgwSynopData} entity to which the station information will be assigned
     * @param station the {@link ImgwSynopStation} entity representing the station to be associated with the data
     */
    @Override protected void setStationToData(ImgwSynopData data, ImgwSynopStation station) { data.setStation(station); }

    /**
     * Retrieves the JpaRepository responsible for managing {@link ImgwSynopStation} entities.
     *
     * @return the JpaRepository for {@link ImgwSynopStation}, allowing CRUD operations on the synoptic station entities.
     */
    @Override protected JpaRepository<ImgwSynopStation, String> getStationRepository() { return stationRepo; }

    /**
     * Retrieves the JpaRepository responsible for managing {@link ImgwSynopData} entities.
     *
     * @return the JpaRepository for {@link ImgwSynopData}, allowing CRUD operations on the synoptic data entities.
     */
    @Override protected JpaRepository<ImgwSynopData, Long> getDataRepository() { return dataRepo; }
}