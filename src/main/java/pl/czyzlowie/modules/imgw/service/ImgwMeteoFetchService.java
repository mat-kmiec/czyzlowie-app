package pl.czyzlowie.modules.imgw.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.imgw.client.ImgwClient;
import pl.czyzlowie.modules.imgw.client.dto.ImgwMeteoResponseDto;
import pl.czyzlowie.modules.imgw.config.ImgwApiProperties;
import pl.czyzlowie.modules.imgw.entity.ImgwMeteoData;
import pl.czyzlowie.modules.imgw.entity.ImgwMeteoStation;
import pl.czyzlowie.modules.imgw.mapper.ImgwMeteoMapper;
import pl.czyzlowie.modules.imgw.repository.ImgwMeteoDataRepository;
import pl.czyzlowie.modules.imgw.repository.ImgwMeteoStationRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service responsible for fetching and processing meteorological data from the IMiGW API.
 * This class facilitates fetching data from an external source, mapping it to domain entities,
 * and storing it in the appropriate repositories. It also provides mechanisms to compare and
 * update meteorological data when newer information is available.
 *
 * This service extends the {@code AbstractImgwFetchService} class, leveraging its structure
 * for implementations specific to meteorological data use cases.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImgwMeteoFetchService extends AbstractImgwFetchService<ImgwMeteoResponseDto, ImgwMeteoStation, ImgwMeteoData> {

    private final ImgwClient imgwClient;
    private final ImgwApiProperties properties;
    private final ImgwMeteoStationRepository stationRepo;
    private final ImgwMeteoDataRepository dataRepo;
    private final ImgwMeteoMapper mapper;

    /**
     * Fetches a list of meteorological data transfer objects (DTOs) from the external API.
     *
     * @return a list of {@code ImgwMeteoResponseDto} objects retrieved from the external API
     */
    @Override
    protected List<ImgwMeteoResponseDto> fetchFromApi() {
        return imgwClient.fetchList(properties.getMeteoUrl(), new ParameterizedTypeReference<>() {});
    }

    /**
     * Retrieves the latest meteorological data for a given set of station identifiers.
     *
     * @param stationIds a set of unique station identifiers for which the latest data is to be retrieved
     * @return a map where the keys are station identifiers as strings and the values are the corresponding
     *         latest instances of {@code ImgwMeteoData}
     */
    @Override
    protected Map<String, ImgwMeteoData> getLatestDataMap(Set<String> stationIds) {
        return dataRepo.findLatestDataForStations(stationIds).stream()
                .collect(Collectors.toMap(
                        d -> d.getStation().getId(),
                        Function.identity()
                ));
    }

    /**
     * Determines whether the provided new meteorological data is more recent than the last known data.
     *
     * @param lastKnownData the previous instance of {@code ImgwMeteoData} that is considered the last known data
     * @param newData the current instance of {@code ImgwMeteoData} to be compared with the last known data
     * @return {@code true} if the new data is more recent than the last known data, otherwise {@code false}
     */
    @Override
    protected boolean isNewer(ImgwMeteoData lastKnownData, ImgwMeteoData newData) {
        return newData.isNewerThan(lastKnownData);
    }

    /**
     * Extracts the station identifier from the given data transfer object (DTO).
     *
     * @param dto the {@code ImgwMeteoResponseDto} object from which the station ID will be extracted
     * @return the unique station identifier as a {@code String}
     */
    @Override protected String getStationIdFromDto(ImgwMeteoResponseDto dto) { return dto.getStationId(); }

    /**
     * Retrieves the station identifier from the given meteorological station entity.
     *
     * @param station the instance of {@code ImgwMeteoStation} from which the station ID will be extracted
     * @return the unique identifier of the specified station as a {@code String}
     */
    @Override protected String getStationIdFromEntity(ImgwMeteoStation station) { return station.getId(); }

    /**
     * Maps the given data transfer object (DTO) to the corresponding meteorological station entity.
     *
     * @param dto the {@code ImgwMeteoResponseDto} object containing the data to be mapped to a station
     * @return the {@code ImgwMeteoStation} entity created from the provided DTO
     */
    @Override protected ImgwMeteoStation mapToStation(ImgwMeteoResponseDto dto) { return mapper.toMeteoStation(dto); }

    /**
     * Maps the given data transfer object (DTO) to the corresponding meteorological data entity.
     *
     * @param dto the {@code ImgwMeteoResponseDto} object containing the meteorological data to be mapped
     * @return an {@code ImgwMeteoData} entity representing the mapped meteorological data
     */
    @Override protected ImgwMeteoData mapToData(ImgwMeteoResponseDto dto) { return mapper.toMeteoData(dto); }

    /**
     * Assigns the specified station to the provided meteorological data object.
     *
     * @param data the instance of {@code ImgwMeteoData} to which the station will be assigned
     * @param station the instance of {@code ImgwMeteoStation} that represents the station to be assigned
     */
    @Override protected void setStationToData(ImgwMeteoData data, ImgwMeteoStation station) { data.setStation(station); }

    /**
     * Provides the repository used for accessing and managing entities of type {@code ImgwMeteoStation}.
     *
     * @return a {@code JpaRepository} instance for handling {@code ImgwMeteoStation} entities
     */
    @Override protected JpaRepository<ImgwMeteoStation, String> getStationRepository() { return stationRepo; }

    /**
     * Provides the data repository used for accessing and managing entities of type {@code ImgwMeteoData}.
     *
     * @return a {@code JpaRepository} instance for handling {@code ImgwMeteoData} entities
     */
    @Override protected JpaRepository<ImgwMeteoData, Long> getDataRepository() { return dataRepo; }
}