package pl.czyzlowie.modules.forecast.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.forecast.entity.WeatherForecast;
import pl.czyzlowie.modules.forecast.mapper.WeatherForecastMapper;
import pl.czyzlowie.modules.forecast.repository.WeatherForecastRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing the storage of weather forecast data
 * in the database. It provides methods for saving or updating weather forecasts
 * and ensures data consistency by processing forecasts based on station type and time range.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherForecastStorageService {

    private final WeatherForecastRepository forecastRepository;
    private final WeatherForecastMapper mapper;

    /**
     * Saves or updates a list of incoming weather forecasts in the database. The method checks for
     * existing forecasts within the given time range and updates them if found; otherwise,
     * it saves the new forecasts. It processes either Synop station data or virtual station data
     * based on the isSynop flag.
     *
     * @param incomingForecasts the list of weather forecasts to be saved or updated
     * @param isSynop a flag indicating whether the forecasts are for Synop stations (true)
     *                or virtual stations (false)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveForecasts(List<WeatherForecast> incomingForecasts, boolean isSynop) {
        if (incomingForecasts.isEmpty()) return;

        LocalDateTime minDate = incomingForecasts.stream()
                .map(WeatherForecast::getForecastTime)
                .min(LocalDateTime::compareTo).orElseThrow();
        LocalDateTime maxDate = incomingForecasts.stream()
                .map(WeatherForecast::getForecastTime)
                .max(LocalDateTime::compareTo).orElseThrow();

        Set<String> stationIds = incomingForecasts.stream()
                .map(f -> isSynop ? f.getSynopStation().getId() : f.getVirtualStation().getId())
                .collect(Collectors.toSet());

        List<WeatherForecast> existingForecasts = isSynop
                ? forecastRepository.findAllBySynopStationIdInAndForecastTimeBetween(stationIds, minDate, maxDate)
                : forecastRepository.findAllByVirtualStationIdInAndForecastTimeBetween(stationIds, minDate, maxDate);

        Map<String, WeatherForecast> dbMap = existingForecasts.stream()
                .collect(Collectors.toMap(this::generateUniqueKey, Function.identity()));

        List<WeatherForecast> toSave = new ArrayList<>();

        for (WeatherForecast incoming : incomingForecasts) {
            String key = generateUniqueKey(incoming);
            WeatherForecast existing = dbMap.get(key);

            if (existing != null) {
                mapper.updateForecast(existing, incoming);
                toSave.add(existing);
            } else {
                toSave.add(incoming);
            }
        }

        forecastRepository.saveAll(toSave);
        log.info("Zapisano/Zaktualizowano {} prognoz (Typ Synop: {}).", toSave.size(), isSynop);
    }

    /**
     * Generates a unique key for the given WeatherForecast object. The key
     * is a combination of the station ID and the forecast time, separated
     * by a "|" character. If the Synop station is not available, the ID
     * from the virtual station is used instead.
     *
     * @param wf the WeatherForecast object for which the unique key is generated
     * @return a unique key as a String, constructed from the station ID and forecast time
     */
    private String generateUniqueKey(WeatherForecast wf) {
        String stationId = wf.getSynopStation() != null ? wf.getSynopStation().getId() : wf.getVirtualStation().getId();
        return stationId + "|" + wf.getForecastTime();
    }
}