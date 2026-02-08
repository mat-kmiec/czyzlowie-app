package pl.czyzlowie.modules.forecast.service;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import pl.czyzlowie.modules.forecast.client.OpenMeteoClient;
import pl.czyzlowie.modules.forecast.client.dto.OpenMeteoResponse;
import pl.czyzlowie.modules.forecast.entity.VirtualStation;
import pl.czyzlowie.modules.forecast.entity.WeatherForecast;
import pl.czyzlowie.modules.forecast.mapper.WeatherForecastMapper;
import pl.czyzlowie.modules.forecast.repository.WeatherForecastRepository;
import pl.czyzlowie.modules.forecast.repository.VirtualStationRepository;
import pl.czyzlowie.modules.imgw.entity.ImgwSynopStation;
import pl.czyzlowie.modules.imgw.repository.ImgwSynopStationRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
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
    private String apiUrl;

    private final ExecutorService apiExecutor = Executors.newFixedThreadPool(2);


    @Async
    public void updateAllForecasts() {
        log.info("START: Aktualizacja prognoz pogody (Hourly)...");

        List<ImgwSynopStation> synopStations = synopStationRepository.findAllByIsActiveTrue();
        processStations(synopStations,
                s -> buildUrl(s.getLatitude(), s.getLongitude()),
                mapper::toSynopForecasts,
                true);

        List<VirtualStation> virtualStations = virtualStationRepository.findAllByActiveTrue();
        processStations(virtualStations,
                s -> buildUrl(s.getLatitude(), s.getLongitude()),
                mapper::toVirtualForecasts,
                false);

        log.info("KONIEC: Aktualizacja prognoz zakończona.");
    }

    private <T> void processStations(List<T> stations,
                                     Function<T, String> urlBuilder,
                                     BiFunction<OpenMeteoResponse, T, List<WeatherForecast>> mappingStrategy,
                                     boolean isSynop) {
        if (stations.isEmpty()) return;

        log.info("Rozpoczynam pobieranie dla {} stacji (Typ Synop: {})...", stations.size(), isSynop);

        List<WeatherForecast> fetchedForecasts = stations.stream()
                .map(station -> CompletableFuture.supplyAsync(() -> fetchForecastForStation(station, urlBuilder, mappingStrategy), apiExecutor))
                .toList()
                .stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .toList();

        if (fetchedForecasts.isEmpty()) {
            log.warn("Nie pobrano żadnych prognoz (Typ Synop: {}).", isSynop);
            return;
        }

        saveForecastsBatch(fetchedForecasts, isSynop);
    }

    private <T> List<WeatherForecast> fetchForecastForStation(T station,
                                                              Function<T, String> urlBuilder,
                                                              BiFunction<OpenMeteoResponse, T, List<WeatherForecast>> mappingStrategy) {
        try {

            try {
                TimeUnit.MILLISECONDS.sleep(150);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            String url = urlBuilder.apply(station);
            return openMeteoClient.fetchData(url, OpenMeteoResponse.class)
                    .map(response -> mappingStrategy.apply(response, station))
                    .orElse(Collections.emptyList());
        } catch (Exception e) {
            log.error("API Error [Station]: {}", e.getMessage());
            return Collections.emptyList();
        }
    }


    @Transactional
    protected void saveForecastsBatch(List<WeatherForecast> incomingForecasts, boolean isSynop) {
        if (incomingForecasts.isEmpty()) return;

        LocalDateTime minDate = incomingForecasts.stream().map(WeatherForecast::getForecastTime).min(LocalDateTime::compareTo).orElseThrow();
        LocalDateTime maxDate = incomingForecasts.stream().map(WeatherForecast::getForecastTime).max(LocalDateTime::compareTo).orElseThrow();

        Set<String> stationIds = incomingForecasts.stream()
                .map(f -> isSynop ? f.getSynopStation().getId() : f.getVirtualStation().getId())
                .collect(Collectors.toSet());

        List<WeatherForecast> existingForecasts;
        if (isSynop) {
            existingForecasts = forecastRepository.findAllBySynopStationIdInAndForecastTimeBetween(stationIds, minDate, maxDate);
        } else {
            existingForecasts = forecastRepository.findAllByVirtualStationIdInAndForecastTimeBetween(stationIds, minDate, maxDate);
        }

        Map<String, WeatherForecast> dbMap = existingForecasts.stream()
                .collect(Collectors.toMap(this::generateUniqueKey, Function.identity()));

        List<WeatherForecast> toSave = new ArrayList<>();

        for (WeatherForecast incoming : incomingForecasts) {
            String key = generateUniqueKey(incoming);
            WeatherForecast existing = dbMap.get(key);

            if (existing != null) {
                updateEntityFields(existing, incoming);
                toSave.add(existing);
            } else {
                toSave.add(incoming);
            }
        }

        forecastRepository.saveAll(toSave);
        log.info("Zaktualizowano/Dodano {} prognoz (Typ Synop: {}).", toSave.size(), isSynop);
    }

    private String generateUniqueKey(WeatherForecast wf) {
        String stationId = wf.getSynopStation() != null ? wf.getSynopStation().getId() : wf.getVirtualStation().getId();
        return stationId + "_" + wf.getForecastTime();
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

    private String buildUrl(BigDecimal lat, BigDecimal lon) {
        return UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("latitude", lat)
                .queryParam("longitude", lon)
                .queryParam("past_days", 1)
                .queryParam("hourly", "temperature_2m,apparent_temperature,rain,weather_code," +
                        "cloud_cover,wind_speed_10m,wind_direction_10m,wind_gusts_10m," +
                        "surface_pressure,uv_index")
                .queryParam("daily", "sunrise,sunset,uv_index_max")
                .queryParam("timezone", "Europe/Warsaw")
                .build()
                .toUriString();
    }

    @PreDestroy
    public void shutdown() {
        apiExecutor.shutdown();
    }
}