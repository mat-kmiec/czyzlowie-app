package pl.czyzlowie.modules.fish_forecast.infrastructure.integration.hydro;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.fish_forecast.domain.model.HydroSnapshot;
import pl.czyzlowie.modules.imgw_api.entity.ImgwHydroData;
import pl.czyzlowie.modules.imgw_api.repository.ImgwHydroDataRepository;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service class responsible for integrating with IMGW hydrological data repository
 * and transforming the fetched data into domain-specific objects for further use
 * in application logic.
 *
 * The class includes methods for asynchronously fetching and processing hydrological
 * data snapshots within a specified time range. The service consolidates access to
 * repository methods and provides enriched domain-specific representations of the data.
 *
 * Responsibilities:
 * - Performs data retrieval from the data source (IMGW repository).
 * - Converts raw entity data into domain models using a mapper component.
 * - Handles logic for defining time ranges and ensuring proper data selection.
 *
 * Dependencies:
 * - ImgwHydroDataRepository: Repository interface for accessing IMGW hydrological data.
 * - HydroDataMapper: Component for transforming raw entity data into domain-level objects.
 *
 * Constants:
 * - HISTORY_HOURS: Specifies the time range (in hours) from the target time to fetch data.
 */
@Service
@RequiredArgsConstructor
public class HydroIntegrationService {

    private final ImgwHydroDataRepository repository;
    private final HydroDataMapper mapper;

    private static final int HISTORY_HOURS = 72;

    /**
     * Fetches a timeline of hydrological data snapshots for a specific station within a predefined
     * time range. The method asynchronously retrieves data and maps it to the domain model.
     *
     * @param stationId The ID of the station from which to fetch hydrological data.
     *                  If null, an empty list is returned.
     * @param targetTime The target time determining the end of the time range for fetching data.
     *                   The time range spans the last 72 hours from this time.
     * @return A CompletableFuture containing a list of {@code HydroSnapshot} objects representing
     *         hydrological data for the specified station and time range. If no data is found
     *         or the stationId is null, an empty list is returned.
     */
    @Async("dataFetchExecutor")
    public CompletableFuture<List<HydroSnapshot>> fetchHydroTimeline(Long stationId, ZonedDateTime targetTime) {
        if (stationId == null) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        LocalDateTime endTime = targetTime.toLocalDateTime();
        LocalDateTime startTime = endTime.minusHours(HISTORY_HOURS);

        List<ImgwHydroData> rawData = repository.findByStationIdAndDateRange(stationId, startTime, endTime);

        List<HydroSnapshot> timeline = rawData.stream()
                .map(mapper::toDomain)
                .toList();

        return CompletableFuture.completedFuture(timeline);
    }
}
