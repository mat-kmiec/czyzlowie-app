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

@Service
@RequiredArgsConstructor
public class HydroIntegrationService {

    private final ImgwHydroDataRepository repository;
    private final HydroDataMapper mapper;

    private static final int HISTORY_HOURS = 72;

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
