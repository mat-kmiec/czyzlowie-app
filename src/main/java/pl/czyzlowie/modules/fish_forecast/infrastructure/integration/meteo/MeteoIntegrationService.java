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

@Service
@RequiredArgsConstructor
public class MeteoIntegrationService {

    private final ImgwMeteoDataRepository repository;
    private final MeteoDataMapper mapper;

    private static final int HISTORY_HOURS = 72;

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
