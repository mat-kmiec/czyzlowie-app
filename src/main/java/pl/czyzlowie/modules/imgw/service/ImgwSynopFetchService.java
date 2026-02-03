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

@Service
@RequiredArgsConstructor
@Slf4j
public class ImgwSynopFetchService extends AbstractImgwFetchService<ImgwSynopResponseDto, ImgwSynopStation, ImgwSynopData> {

    private final ImgwClient imgwClient;
    private final ImgwApiProperties properties;
    private final ImgwSynopStationRepository stationRepo;
    private final ImgwSynopDataRepository dataRepo;
    private final ImgwSynopMapper mapper;

    @Override
    protected List<ImgwSynopResponseDto> fetchFromApi() {
        return imgwClient.fetchList(
                properties.getSynopUrl(),
                new ParameterizedTypeReference<>() {}
        );
    }

    @Override
    protected Map<String, ImgwSynopData> getLatestDataMap(Set<String> stationIds) {
        return dataRepo.findLatestDataForStations(stationIds).stream()
                .collect(Collectors.toMap(
                        d -> d.getStation().getId(),
                        Function.identity()
                ));
    }

    @Override
    protected boolean isNewer(ImgwSynopData lastKnownData, ImgwSynopData newData) {
        if (lastKnownData == null) return true;

        boolean dateChanged = !newData.getMeasurementDate().equals(lastKnownData.getMeasurementDate());
        boolean hourChanged = !newData.getMeasurementHour().equals(lastKnownData.getMeasurementHour());

        return dateChanged || hourChanged;
    }

    @Override protected String getStationIdFromDto(ImgwSynopResponseDto dto) { return dto.getStationId(); }
    @Override protected String getStationIdFromEntity(ImgwSynopStation station) { return station.getId(); }
    @Override protected ImgwSynopStation mapToStation(ImgwSynopResponseDto dto) { return mapper.toSynopStation(dto); }
    @Override protected ImgwSynopData mapToData(ImgwSynopResponseDto dto) { return mapper.toSynopData(dto); }
    @Override protected void setStationToData(ImgwSynopData data, ImgwSynopStation station) { data.setStation(station); }
    @Override protected JpaRepository<ImgwSynopStation, String> getStationRepository() { return stationRepo; }
    @Override protected JpaRepository<ImgwSynopData, Long> getDataRepository() { return dataRepo; }
}