package pl.czyzlowie.modules.fish_forecast.infrastructure.integration.synop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.fish_forecast.domain.model.SynopSnapshot;
import pl.czyzlowie.modules.forecast.repository.VirtualStationDataRepository;
import pl.czyzlowie.modules.forecast.repository.WeatherForecastRepository;
import pl.czyzlowie.modules.imgw_api.repository.ImgwSynopDataRepository;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Service responsible for integrating and fetching meteorological synoptic data from various sources.
 *
 * This service retrieves historical and forecasted synoptic data, combining information from
 * IMGW stations, virtual stations, and weather forecast repositories. The data is aggregated into
 * a chronological timeline for the specified station and time range.
 *
 * The service supports both virtual and IMGW stations. For virtual stations, historical and forecasted
 * data are fetched from their respective repositories. For IMGW stations, historical data is fetched from
 * the IMGW repository, and forecasted data is collected from the weather forecast repository.
 *
 * This service performs its operations asynchronously, leveraging a dedicated task executor.
 *
 * Key Operations:
 * - Fetch historical and forecasted data for a specified station within a combined time window
 *   (including up to 72 hours of historical data and up to 72 hours of forecast data).
 * - Return a timeline of synoptic snapshots, consisting of timestamped meteorological data points.
 *
 * Errors and Special Conditions:
 * - If the station ID is null, empty, or invalid (e.g., non-numeric in the case of IMGW stations),
 *   an empty list is returned.
 * - The service logs details about the execution process, including errors during station ID parsing.
 *
 * Dependencies:
 * - {@link ImgwSynopDataRepository} for fetching historical data from IMGW stations.
 * - {@link VirtualStationDataRepository} for fetching historical data from virtual stations.
 * - {@link WeatherForecastRepository} for fetching forecasted weather data for both virtual and IMGW stations.
 * - {@link SynopDataMapper} for mapping raw repository data into domain-specific {@link SynopSnapshot} models.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SynopIntegrationService {

    private final ImgwSynopDataRepository imgwRepo;
    private final VirtualStationDataRepository virtualRepo;
    private final WeatherForecastRepository forecastRepo;
    private final SynopDataMapper mapper;

    private static final int HISTORY_HOURS = 72;
    private static final int FORECAST_HOURS = 72;

    /**
     * Fetches the synoptic data timeline for a specified station and a target time.
     * The method retrieves historical data and forecast data for either virtual or IMGW stations,
     * aggregating them into a chronological timeline.
     *
     * @param stationId the ID of the station for which the data is to be retrieved.
     *                  It can be the ID of a virtual station or an IMGW station.
     * @param isVirtual boolean flag indicating whether the station is a virtual station (true)
     *                  or an IMGW station (false).
     * @param targetTime the target time as a {@link ZonedDateTime} around which the historical and forecasted
     *                   data will be fetched.
     * @return a {@link CompletableFuture} wrapping a {@link List} of {@link SynopSnapshot} objects that represent
     *         the synoptic data timeline for the specified station.
     *         If the station ID is invalid, an empty list is returned inside the CompletableFuture.
     */
    @Async("dataFetchExecutor")
    public CompletableFuture<List<SynopSnapshot>> fetchSynopTimeline(String stationId, boolean isVirtual, ZonedDateTime targetTime) {

        if (stationId == null || stationId.trim().isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        LocalDateTime targetLocal = targetTime.toLocalDateTime();
        LocalDateTime startTime = targetLocal.minusHours(HISTORY_HOURS);
        LocalDateTime endTime = targetLocal.plusHours(FORECAST_HOURS);

        Map<LocalDateTime, SynopSnapshot> timelineMap = new TreeMap<>();

        if (isVirtual) {
            log.debug("Pobieranie danych dla wirtualnej stacji: {}", stationId);

            virtualRepo.findHistory(stationId, startTime, targetLocal).forEach(data -> {
                SynopSnapshot snap = mapper.fromVirtual(data);
                timelineMap.put(snap.timestamp(), snap);
            });

            forecastRepo.findForecastForVirtual(stationId, targetLocal, endTime).forEach(data -> {
                SynopSnapshot snap = mapper.fromForecast(data);
                timelineMap.put(snap.timestamp(), snap);
            });

        } else {
            Long imgwStationId;
            try {
                imgwStationId = Long.valueOf(stationId);
            } catch (NumberFormatException e) {
                log.error("Nie można sparsować ID stacji IMGW do typu Long: {}", stationId);
                return CompletableFuture.completedFuture(Collections.emptyList());
            }

            log.debug("Pobieranie danych dla stacji IMGW: {}", imgwStationId);

            imgwRepo.findHistory(imgwStationId, startTime, targetLocal).forEach(data -> {
                SynopSnapshot snap = mapper.fromImgw(data);
                timelineMap.put(snap.timestamp(), snap);
            });

            forecastRepo.findForecastForImgw(imgwStationId, targetLocal, endTime).forEach(data -> {
                SynopSnapshot snap = mapper.fromForecast(data);
                timelineMap.put(snap.timestamp(), snap);
            });
        }

        return CompletableFuture.completedFuture(new ArrayList<>(timelineMap.values()));
    }
}