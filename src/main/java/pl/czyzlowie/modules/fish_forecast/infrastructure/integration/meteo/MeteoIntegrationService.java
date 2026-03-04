package pl.czyzlowie.modules.fish_forecast.infrastructure.integration.meteo;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.fish_forecast.domain.model.MeteoSnapshot;
import pl.czyzlowie.modules.imgw_api.repository.ImgwMeteoDataRepository;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service responsible for fetching and mapping meteorological data for a specified station
 * over a specific historical time frame. The service utilizes an asynchronous processing model
 * to handle requests in a non-blocking way.
 *
 * This service interacts with the repository layer to fetch raw meteorological data and maps
 * it into the domain model representation using a mapper. The resulting data includes high-frequency
 * meteorological snapshots, useful for forecasting and analysis.
 */
@Service
@RequiredArgsConstructor
public class MeteoIntegrationService {

    private final ImgwMeteoDataRepository repository;
    private final MeteoDataMapper mapper;

    private static final int HISTORY_HOURS = 72;

    /**
     * Fetches a timeline of meteorological data snapshots for a given station within a specific time frame.
     * The data is retrieved asynchronously and converted into the domain model representation.
     *
     * @param stationId   The ID of the meteorological station for which the data is being fetched.
     *                    If null, an empty list is immediately returned.
     * @param targetTime  The target time up to which the timeline will be fetched. The timeline
     *                    spans the last 72 hours (defined by the HISTORY_HOURS constant) from this time.
     * @return A CompletableFuture holding a list of MeteoSnapshot objects representing
     *         the meteorological data timeline.
     *         Returns an empty list if the stationId is null.
     */
    @Async("dataFetchExecutor")
    public CompletableFuture<List<MeteoSnapshot>> fetchMeteoTimeline(Long stationId, ZonedDateTime targetTime) {
        if (stationId == null) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        LocalDateTime endTime = targetTime.toLocalDateTime();
        LocalDateTime startTime = endTime.minusHours(HISTORY_HOURS);

        List<MeteoSnapshot> timeline = repository.findHistoryForForecast(stationId, startTime, endTime)
                .stream()
                .map(mapper::toDomain)
                .toList();

        return CompletableFuture.completedFuture(timeline);
    }
}
