package pl.czyzlowie.modules.imgw_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.imgw_api.client.ImgwClient;
import pl.czyzlowie.modules.imgw_api.client.dto.ImgwHydroResponseDto;
import pl.czyzlowie.modules.imgw_api.config.ImgwApiProperties;
import pl.czyzlowie.modules.imgw_api.entity.ImgwHydroData;
import pl.czyzlowie.modules.imgw_api.entity.ImgwHydroStation;
import pl.czyzlowie.modules.imgw_api.mapper.ImgwHydroMapper;
import pl.czyzlowie.modules.imgw_api.repository.ImgwHydroDataRepository;
import pl.czyzlowie.modules.imgw_api.repository.ImgwHydroStationRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service responsible for fetching and processing hydrological data from the IMGW API.
 * This class extends {@code AbstractImgwFetchService} to provide specific implementations
 * for working with hydrological data entities, including stations and measurement data.
 * It handles the fetching of data from an external API, mapping it to internal entities,
 * comparing it with the most recent stored data, and saving new or updated data to the database.
 * Key Responsibilities:
 * - Fetch hydrological data from the IMGW API.
 * - Map API responses to internal DTOs and entities.
 * - Identify and persist new or updated station and data records.
 * - Leverage repositories for database interactions and manage transactions.
 * Dependencies:
 * - {@code ImgwClient} for making HTTP requests to the external API.
 * - {@code ImgwApiProperties} for API URL configurations.
 * - {@code ImgwHydroStationRepository} for station entity persistence.
 * - {@code ImgwHydroDataRepository} for hydrological data entity persistence.
 * - {@code ImgwHydroMapper} for mapping between DTOs and entities.
 * This service is constructed with Spring's Dependency Injection mechanism,
 * facilitated by the {@code @RequiredArgsConstructor} and {@code @Service} annotations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImgwHydroFetchService extends AbstractImgwFetchService<ImgwHydroResponseDto, ImgwHydroStation, ImgwHydroData> {

    private final ImgwClient imgwClient;
    private final ImgwApiProperties properties;
    private final ImgwHydroStationRepository stationRepo;
    private final ImgwHydroDataRepository dataRepo;
    private final ImgwHydroMapper mapper;

    /**
     * Fetches a list of hydrological data from the IMGW API.
     * The method retrieves the data using the configured API URL and parses the response
     * into a list of {@code ImgwHydroResponseDto} objects.
     *
     * @return a list of {@code ImgwHydroResponseDto} containing hydrological data fetched from the API,
     *         or an empty list if no data is retrieved or in case of an error.
     */
    @Override
    protected List<ImgwHydroResponseDto> fetchFromApi() {
        return imgwClient.fetchList(
                properties.getHydroUrl(),
                new ParameterizedTypeReference<>() {}
        );
    }

    /**
     * Retrieves the latest hydrological data for a specified set of station IDs.
     * This method fetches the most recent data for the provided station identifiers
     * and constructs a map where the key is the station ID, and the value is
     * the corresponding {@code ImgwHydroData} entity.
     *
     * @param stationIds a set of station IDs for which the latest hydrological data is to be fetched
     * @return a map containing station IDs as keys and their latest associated {@code ImgwHydroData} as values
     */
    @Override
    protected Map<String, ImgwHydroData> getLatestDataMap(Set<String> stationIds) {
        return dataRepo.findLatestDataForStations(stationIds).stream()
                .collect(Collectors.toMap(
                        d -> d.getStation().getId(),
                        Function.identity()
                ));
    }

    /**
     * Determines whether the new hydrological data is more recent than the last known data.
     *
     * @param lastKnownData the most recently known hydrological data stored in the system
     * @param newData the newest hydrological data to be evaluated
     * @return true if the new data is more recent than the last known data, false otherwise
     */
    @Override
    protected boolean isNewer(ImgwHydroData lastKnownData, ImgwHydroData newData) {
        return newData.isNewerThan(lastKnownData);
    }

    /**
     * Extracts the station ID from the given ImgwHydroResponseDto object.
     *
     * @param dto the data transfer object containing station information
     * @return the station ID as a string
     */
    @Override protected String getStationIdFromDto(ImgwHydroResponseDto dto) { return dto.getStationId(); }

    /**
     * Extracts the station ID from the given ImgwHydroStation entity.
     *
     * @param station the ImgwHydroStation entity from which the station ID will be extracted
     * @return the station ID as a string
     */
    @Override protected String getStationIdFromEntity(ImgwHydroStation station) { return station.getId(); }

    /**
     * Maps the given ImgwHydroResponseDto object to an ImgwHydroStation entity.
     *
     * @param dto the data transfer object containing station information to be mapped
     * @return an ImgwHydroStation entity resulting from the mapping of the provided DTO
     */
    @Override protected ImgwHydroStation mapToStation(ImgwHydroResponseDto dto) { return mapper.toHydroStation(dto); }

    /**
     * Maps the given ImgwHydroResponseDto object to an ImgwHydroData entity.
     *
     * @param dto the data transfer object containing hydrological data to be mapped
     * @return an ImgwHydroData entity resulting from the mapping of the provided DTO
     */
    @Override protected ImgwHydroData mapToData(ImgwHydroResponseDto dto) { return mapper.toHydroData(dto); }

    /**
     * Associates a specified hydrological station with a given hydrological data entity.
     *
     * @param data the {@code ImgwHydroData} entity to which the station will be assigned
     * @param station the {@code ImgwHydroStation} entity to associate with the hydrological data
     */
    @Override protected void setStationToData(ImgwHydroData data, ImgwHydroStation station) { data.setStation(station); }

    /**
     * Provides the repository for accessing and managing hydrological station entities.
     *
     * @return the {@code JpaRepository} used for CRUD operations on {@code ImgwHydroStation} entities,
     *         where the entity ID type is {@code String}.
     */
    @Override protected JpaRepository<ImgwHydroStation, String> getStationRepository() { return stationRepo; }

    /**
     * Provides the repository for accessing and managing hydrological data entities.
     *
     * @return the {@code JpaRepository} used for CRUD operations on {@code ImgwHydroData} entities,
     *         where the entity ID type is {@code Long}
     */
    @Override protected JpaRepository<ImgwHydroData, Long> getDataRepository() { return dataRepo; }
}