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

@Service
@RequiredArgsConstructor
@Slf4j
public class ImgwMeteoFetchService extends AbstractImgwFetchService<ImgwMeteoResponseDto, ImgwMeteoStation, ImgwMeteoData> {

    private final ImgwClient imgwClient;
    private final ImgwApiProperties properties;
    private final ImgwMeteoStationRepository stationRepo;
    private final ImgwMeteoDataRepository dataRepo;
    private final ImgwMeteoMapper mapper;

    @Override
    protected List<ImgwMeteoResponseDto> fetchFromApi() {
        return imgwClient.fetchList(properties.getMeteoUrl(), new ParameterizedTypeReference<>() {});
    }

    @Override
    protected Map<String, ImgwMeteoData> getLatestDataMap(Set<String> stationIds) {
        return dataRepo.findLatestDataForStations(stationIds).stream()
                .collect(Collectors.toMap(
                        d -> d.getStation().getId(),
                        Function.identity()
                ));
    }

    @Override
    protected boolean isNewer(ImgwMeteoData lastKnownData, ImgwMeteoData newData) {
        return newData.isNewerThan(lastKnownData);
    }

    @Override protected String getStationIdFromDto(ImgwMeteoResponseDto dto) { return dto.getStationId(); }
    @Override protected String getStationIdFromEntity(ImgwMeteoStation station) { return station.getId(); }
    @Override protected ImgwMeteoStation mapToStation(ImgwMeteoResponseDto dto) { return mapper.toMeteoStation(dto); }
    @Override protected ImgwMeteoData mapToData(ImgwMeteoResponseDto dto) { return mapper.toMeteoData(dto); }
    @Override protected void setStationToData(ImgwMeteoData data, ImgwMeteoStation station) { data.setStation(station); }
    @Override protected JpaRepository<ImgwMeteoStation, String> getStationRepository() { return stationRepo; }
    @Override protected JpaRepository<ImgwMeteoData, Long> getDataRepository() { return dataRepo; }
}