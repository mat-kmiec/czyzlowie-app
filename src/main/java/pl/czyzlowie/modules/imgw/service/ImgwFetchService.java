package pl.czyzlowie.modules.imgw.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.imgw.client.ImgwClient;
import pl.czyzlowie.modules.imgw.client.dto.*;
import pl.czyzlowie.modules.imgw.entity.*;
import pl.czyzlowie.modules.imgw.mapper.ImgwMapper;
import pl.czyzlowie.modules.imgw.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImgwFetchService {

    private final ImgwClient imgwClient;
    private final ImgwMapper mapper;

    private final ImgwSynopStationRepository synopStationRepo;
    private final ImgwSynopDataRepository synopDataRepo;
    private final ImgwMeteoStationRepository meteoStationRepo;
    private final ImgwMeteoDataRepository meteoDataRepo;
    private final ImgwHydroStationRepository hydroStationRepo;
    private final ImgwHydroDataRepository hydroDataRepo;

    private static final String SYNOP_URL = "https://danepubliczne.imgw.pl/api/data/synop";
    private static final String METEO_URL = "https://danepubliczne.imgw.pl/api/data/meteo";
    private static final String HYDRO_URL = "https://danepubliczne.imgw.pl/api/data/hydro";

    @Transactional
    public void fetchSynop() {
        log.info("--- START SYNOP (Manual) ---");
        processFetch(
                SYNOP_URL,
                new ParameterizedTypeReference<List<ImgwSynopResponseDto>>() {},
                ImgwSynopResponseDto::getStationId,
                mapper::toSynopStation,
                synopStationRepo,
                mapper::toSynopData,
                ImgwSynopData::setStation,
                (station, data) -> !synopDataRepo.existsByStationIdAndMeasurementDateAndMeasurementHour(
                        station.getId(), data.getMeasurementDate(), data.getMeasurementHour()),
                synopDataRepo
        );
    }

    @Transactional
    public void fetchMeteo() {
        log.info("--- START METEO (Manual) ---");
        processFetch(
                METEO_URL,
                new ParameterizedTypeReference<List<ImgwMeteoResponseDto>>() {},
                ImgwMeteoResponseDto::getStationId,
                mapper::toMeteoStation,
                meteoStationRepo,
                mapper::toMeteoData,
                ImgwMeteoData::setStation,
                (station, newData) -> {
                    var lastData = meteoDataRepo.findTopByStationIdOrderByIdDesc(station.getId());
                    return lastData.isEmpty() || isNewMeteoData(lastData.get(), newData);
                },
                meteoDataRepo
        );
    }

    @Transactional
    public void fetchHydro() {
        log.info("--- START HYDRO (Manual) ---");
        processFetch(
                HYDRO_URL,
                new ParameterizedTypeReference<List<ImgwHydroResponseDto>>() {},
                ImgwHydroResponseDto::getStationId,
                mapper::toHydroStation,
                hydroStationRepo,
                mapper::toHydroData,
                ImgwHydroData::setStation,
                (station, newData) -> {
                    var lastData = hydroDataRepo.findTopByStationIdOrderByIdDesc(station.getId());
                    return lastData.isEmpty() || isNewHydroData(lastData.get(), newData);
                },
                hydroDataRepo
        );
    }


    private <DTO, S, D> void processFetch(
            String url,
            ParameterizedTypeReference<List<DTO>> responseType,
            Function<DTO, String> getStationId,
            Function<DTO, S> mapToStation,
            JpaRepository<S, String> stationRepo,
            Function<DTO, D> mapToData,
            BiConsumer<D, S> setStationToData,
            BiPredicate<S, D> shouldSaveData,
            JpaRepository<D, Long> dataRepo
    ) {
        List<DTO> dtos = imgwClient.fetchList(url, responseType);
        if (dtos.isEmpty()) return;

        int savedCount = 0;
        for (DTO dto : dtos) {
            String stationId = getStationId.apply(dto);
            S station = stationRepo.findById(stationId)
                    .orElseGet(() -> stationRepo.save(mapToStation.apply(dto)));

            D dataEntity = mapToData.apply(dto);

            if (shouldSaveData.test(station, dataEntity)) {
                setStationToData.accept(dataEntity, station);
                dataRepo.save(dataEntity);
                savedCount++;
            }
        }
        log.info("Przetworzono {}. Zapisano nowych rekordów: {}", url, savedCount);
    }

    private boolean isNewMeteoData(ImgwMeteoData oldData, ImgwMeteoData newData) {
        if (isDateChanged(oldData.getAirTempTime(), newData.getAirTempTime())) return true;
        if (isDateChanged(oldData.getWindMeasurementTime(), newData.getWindMeasurementTime())) return true;
        if (isDateChanged(oldData.getPrecipitation10minTime(), newData.getPrecipitation10minTime())) return true;
        return isDateChanged(oldData.getWindGust10minTime(), newData.getWindGust10minTime());
    }

    private boolean isNewHydroData(ImgwHydroData oldData, ImgwHydroData newData) {
        if (isDateChanged(oldData.getWaterLevelDate(), newData.getWaterLevelDate())) return true;
        if (isDateChanged(oldData.getDischargeDate(), newData.getDischargeDate())) return true;
        if (isDateChanged(oldData.getWaterTemperatureDate(), newData.getWaterTemperatureDate())) return true;
        if (isDateChanged(oldData.getIcePhenomenonDate(), newData.getIcePhenomenonDate())) return true;
        return isDateChanged(oldData.getOvergrowthPhenomenonDate(), newData.getOvergrowthPhenomenonDate());
    }

    private boolean isDateChanged(LocalDateTime oldDate, LocalDateTime newDate) {
        if (newDate == null) return false;
        if (oldDate == null) return true;
        return !oldDate.isEqual(newDate);
    }
}