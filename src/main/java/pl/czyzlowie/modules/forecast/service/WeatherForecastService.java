package pl.czyzlowie.modules.forecast.service;

import pl.czyzlowie.modules.forecast.client.OpenMeteoClient;
import pl.czyzlowie.modules.forecast.client.dto.OpenMeteoResponse;
import pl.czyzlowie.modules.forecast.entity.VirtualStation;
import pl.czyzlowie.modules.forecast.entity.WeatherForecast;
import pl.czyzlowie.modules.forecast.mapper.WeatherForecastMapper;
import pl.czyzlowie.modules.forecast.repository.VirtualStationRepository;
import pl.czyzlowie.modules.forecast.repository.WeatherForecastRepository;
import pl.czyzlowie.modules.imgw.entity.ImgwSynopStation;
import pl.czyzlowie.modules.imgw.repository.ImgwSynopStationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherForecastService {

    private final ImgwSynopStationRepository synopStationRepository;
    private final VirtualStationRepository virtualStationRepository;
    private final WeatherForecastRepository forecastRepository;
    private final OpenMeteoClient openMeteoClient;
    private final WeatherForecastMapper mapper;

    @Value("${forecast.api.url}")
    private String urlTemplate;


    @Transactional
    public void updateAllForecasts() {
        log.info("START: Aktualizacja prognoz pogody...");

        List<ImgwSynopStation> synopStations = synopStationRepository.findAllByIsActiveTrue();
        log.info("Pobieram prognozę dla {} stacji IMGW Synop", synopStations.size());
        for (ImgwSynopStation station : synopStations) {
            updateForecastForSynop(station);
        }

        List<VirtualStation> virtualStations = virtualStationRepository.findAllByActiveTrue();
        log.info("Pobieram prognozę dla {} stacji Wirtualnych", virtualStations.size());
        for (VirtualStation station : virtualStations) {
            updateForecastForVirtual(station);
        }

        log.info("KONIEC: Aktualizacja prognoz zakończona.");
    }

    private void updateForecastForSynop(ImgwSynopStation station) {
        try {
            String url = String.format(urlTemplate, station.getLatitude(), station.getLongitude());

            Optional<OpenMeteoResponse> responseOpt = openMeteoClient.fetchData(url, OpenMeteoResponse.class);
            if (responseOpt.isEmpty()) return;

            List<WeatherForecast> incomingForecasts = mapper.toSynopForecasts(responseOpt.get(), station);
            if (incomingForecasts.isEmpty()) return;

            LocalDateTime start = incomingForecasts.get(0).getForecastTime();
            LocalDateTime end = incomingForecasts.get(incomingForecasts.size() - 1).getForecastTime();

            List<WeatherForecast> existingForecasts = forecastRepository
                    .findBySynopStation_IdAndForecastTimeBetweenOrderByForecastTimeAsc(station.getId(), start, end);

            performSmartMerge(existingForecasts, incomingForecasts);

        } catch (Exception e) {
            log.error("Błąd aktualizacji prognozy dla stacji Synop: {} (ID: {})", station.getName(), station.getId(), e);
        }
    }
    private void updateForecastForVirtual(VirtualStation station) {
        try {
            String url = String.format(urlTemplate, station.getLatitude(), station.getLongitude());

            Optional<OpenMeteoResponse> responseOpt = openMeteoClient.fetchData(url, OpenMeteoResponse.class);
            if (responseOpt.isEmpty()) return;

            List<WeatherForecast> incomingForecasts = mapper.toVirtualForecasts(responseOpt.get(), station);
            if (incomingForecasts.isEmpty()) return;

            LocalDateTime start = incomingForecasts.get(0).getForecastTime();
            LocalDateTime end = incomingForecasts.get(incomingForecasts.size() - 1).getForecastTime();

            List<WeatherForecast> existingForecasts = forecastRepository
                    .findByVirtualStation_IdAndForecastTimeBetweenOrderByForecastTimeAsc(station.getId(), start, end);

            performSmartMerge(existingForecasts, incomingForecasts);

        } catch (Exception e) {
            log.error("Błąd aktualizacji prognozy dla stacji Virtual: {} (ID: {})", station.getName(), station.getId(), e);
        }
    }

    private void performSmartMerge(List<WeatherForecast> existingList, List<WeatherForecast> incomingList) {

        Map<LocalDateTime, WeatherForecast> dbMap = existingList.stream()
                .collect(Collectors.toMap(WeatherForecast::getForecastTime, Function.identity()));

        List<WeatherForecast> toSave = new ArrayList<>();

        for (WeatherForecast incoming : incomingList) {
            WeatherForecast existing = dbMap.get(incoming.getForecastTime());

            if (existing != null) {
                updateEntityFields(existing, incoming);
                toSave.add(existing);
            } else {
                toSave.add(incoming);
            }
        }

        if (!toSave.isEmpty()) {
            forecastRepository.saveAll(toSave);
        }
    }

    private void updateEntityFields(WeatherForecast target, WeatherForecast source) {
        target.setFetchedAt(LocalDateTime.now());

        target.setTemperature(source.getTemperature());
        target.setApparentTemperature(source.getApparentTemperature());
        target.setPressure(source.getPressure());
        target.setWindSpeed(source.getWindSpeed());
        target.setWindGusts(source.getWindGusts());
        target.setWindDirection(source.getWindDirection());
        target.setRain(source.getRain());
        target.setCloudCover(source.getCloudCover());
        target.setWeatherCode(source.getWeatherCode());
        target.setUvIndex(source.getUvIndex());
    }
}