package pl.czyzlowie.modules.imgw.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.imgw.client.ImgwClient;
import pl.czyzlowie.modules.imgw.client.dto.ImgwHydroResponseDto;
import pl.czyzlowie.modules.imgw.config.ImgwApiProperties;
import pl.czyzlowie.modules.imgw.entity.ImgwHydroData;
import pl.czyzlowie.modules.imgw.entity.ImgwHydroStation;
import pl.czyzlowie.modules.imgw.mapper.ImgwMapper;
import pl.czyzlowie.modules.imgw.repository.ImgwHydroDataRepository;
import pl.czyzlowie.modules.imgw.repository.ImgwHydroStationRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImgwHydroFetchService extends AbstractImgwFetchService<ImgwHydroResponseDto, ImgwHydroStation, ImgwHydroData> {

    private final ImgwClient imgwClient;
    private final ImgwApiProperties properties;
    private final ImgwHydroStationRepository stationRepo;
    private final ImgwHydroDataRepository dataRepo;
    private final ImgwMapper mapper;

    @Override
    protected List<ImgwHydroResponseDto> fetchFromApi() {
        return imgwClient.fetchList(
                properties.getHydroUrl(),
                new ParameterizedTypeReference<>() {}
        );
    }

    @Override
    protected Map<String, ImgwHydroData> getLatestDataMap(Set<String> stationIds) {
        return dataRepo.findLatestDataForStations(stationIds).stream()
                .collect(Collectors.toMap(
                        d -> d.getStation().getId(),
                        Function.identity()
                ));
    }

    @Override
    protected boolean isNewer(ImgwHydroData lastKnownData, ImgwHydroData newData) {
        return newData.isNewerThan(lastKnownData);
    }

    @Override protected String getStationIdFromDto(ImgwHydroResponseDto dto) { return dto.getStationId(); }
    @Override protected String getStationIdFromEntity(ImgwHydroStation station) { return station.getId(); }
    @Override protected ImgwHydroStation mapToStation(ImgwHydroResponseDto dto) { return mapper.toHydroStation(dto); }
    @Override protected ImgwHydroData mapToData(ImgwHydroResponseDto dto) { return mapper.toHydroData(dto); }
    @Override protected void setStationToData(ImgwHydroData data, ImgwHydroStation station) { data.setStation(station); }
    @Override protected JpaRepository<ImgwHydroStation, String> getStationRepository() { return stationRepo; }
    @Override protected JpaRepository<ImgwHydroData, Long> getDataRepository() { return dataRepo; }
}