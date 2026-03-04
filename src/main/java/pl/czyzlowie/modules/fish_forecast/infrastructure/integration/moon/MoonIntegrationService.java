package pl.czyzlowie.modules.fish_forecast.infrastructure.integration.moon;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.fish_forecast.domain.model.MoonSnapshot;
import pl.czyzlowie.modules.moon.entity.MoonGlobalData;
import pl.czyzlowie.modules.moon.entity.MoonStationData;
import pl.czyzlowie.modules.moon.repository.MoonGlobalDataRepository;
import pl.czyzlowie.modules.moon.repository.MoonStationDataRepository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Service responsible for integration and data fetching related to moon data.
 * This service provides functionality for retrieving and processing moon-related
 * global and station data to create a timeline of moon snapshots.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MoonIntegrationService {

    private final MoonGlobalDataRepository globalRepository;
    private final MoonStationDataRepository stationRepository;
    private final MoonDataMapper mapper;
    private static final int HISTORY_DAYS = 3;
    private static final int FORECAST_DAYS = 2;

    /**
     * Fetches a timeline of moon snapshots based on the provided station information and target date.
     * The timeline includes both global moon data and station-specific moon data (if station information is provided).
     *
     * @param stationId The ID of the station for which data is to be fetched. Can be null if station-specific data is not required.
     * @param stationType The type of the station (e.g., SYNOPTIC, VIRTUAL). Can be null if station-specific data is not required.
     * @param targetTime The target date and time for fetching the moon timeline. This will determine the date range for data fetching.
     * @return A CompletableFuture containing a list of MoonSnapshot objects representing the timeline of moon-related data.
     */
    @Async("dataFetchExecutor")
    public CompletableFuture<List<MoonSnapshot>> fetchMoonTimeline(String stationId, String stationType, ZonedDateTime targetTime) {
        LocalDate targetDate = targetTime.toLocalDate();
        LocalDate startDate = targetDate.minusDays(HISTORY_DAYS);
        LocalDate endDate = targetDate.plusDays(FORECAST_DAYS);

        List<MoonGlobalData> globalData = globalRepository.findByCalculationDateBetweenOrderByCalculationDateAsc(startDate, endDate);

        List<MoonStationData> stationData = List.of();

        if (stationId != null && stationType != null) {

            String dbStationType = switch (stationType.toUpperCase()) {
                case "SYNOPTIC", "SYNOP" -> "SYNOP";
                case "VIRTUAL" -> "VIRTUAL";
                default -> stationType;
            };

            log.info("Szukam danych stacji w bazie - ID: {}, Zmapowany Typ: {}, Od: {}, Do: {}",
                    stationId, dbStationType, startDate, endDate);

            stationData = stationRepository.findStationTimeline(stationId, dbStationType, startDate, endDate);
        }

        Map<LocalDate, MoonStationData> stationDataByDate = stationData.stream()
                .collect(Collectors.toMap(
                        data -> data.getId().getCalculationDate(),
                        data -> data,
                        (existing, replacement) -> existing
                ));

        List<MoonSnapshot> timeline = globalData.stream()
                .map(global -> {
                    MoonStationData local = stationDataByDate.get(global.getCalculationDate());
                    return mapper.toDomain(global, local);
                })
                .toList();

        return CompletableFuture.completedFuture(timeline);
    }
}
