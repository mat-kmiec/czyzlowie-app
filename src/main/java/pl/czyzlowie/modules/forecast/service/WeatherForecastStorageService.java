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


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveForecasts(List<WeatherForecast> incomingForecastsRaw, boolean isSynop) {
        if (incomingForecastsRaw.isEmpty()) return;

        // KROK 1: Deduplikacja w locie. Open-Meteo potrafi zwrócić duplikaty w jednej paczce.
        // Zbieramy to do mapy. Jeśli klucze się powtórzą, bierzemy ten nowszy (replacement).
        Map<String, WeatherForecast> deduplicatedIncoming = incomingForecastsRaw.stream()
                .collect(Collectors.toMap(
                        this::generateUniqueKey,
                        Function.identity(),
                        (existing, replacement) -> replacement
                ));

        // Nadpisujemy listę wyczyszczonymi, unikalnymi danymi
        List<WeatherForecast> incomingForecasts = new ArrayList<>(deduplicatedIncoming.values());

        // KROK 2: Standardowa logika sprawdzania bazy (Twój oryginalny kod)
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