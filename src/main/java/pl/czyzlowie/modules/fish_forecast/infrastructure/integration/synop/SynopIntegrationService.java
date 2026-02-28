package pl.czyzlowie.modules.fish_forecast.infrastructure.integration.synop;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class SynopIntegrationService {

    private final ImgwSynopDataRepository imgwRepo;
    private final VirtualStationDataRepository virtualRepo;
    private final WeatherForecastRepository forecastRepo;
    private final SynopDataMapper mapper;

    private static final int HISTORY_HOURS = 72;
    private static final int FORECAST_HOURS = 72;

    @Async("dataFetchExecutor")
    public CompletableFuture<List<SynopSnapshot>> fetchSynopTimeline(Long stationId, boolean isVirtual, ZonedDateTime targetTime) {
        if (stationId == null) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        LocalDateTime targetLocal = targetTime.toLocalDateTime();
        LocalDateTime startTime = targetLocal.minusHours(HISTORY_HOURS);
        LocalDateTime endTime = targetLocal.plusHours(FORECAST_HOURS);

        Map<LocalDateTime, SynopSnapshot> timelineMap = new TreeMap<>();

        if (isVirtual) {
            virtualRepo.findHistory(stationId, startTime, targetLocal).forEach(data -> {
                SynopSnapshot snap = mapper.fromVirtual(data);
                timelineMap.put(snap.timestamp(), snap);
            });
        } else {
            imgwRepo.findHistory(stationId, startTime, targetLocal).forEach(data -> {
                SynopSnapshot snap = mapper.fromImgw(data);
                timelineMap.put(snap.timestamp(), snap);
            });
        }

        if (isVirtual) {
            forecastRepo.findForecastForVirtual(stationId, targetLocal, endTime).forEach(data -> {
                SynopSnapshot snap = mapper.fromForecast(data);
                timelineMap.put(snap.timestamp(), snap);
            });
        } else {
            forecastRepo.findForecastForImgw(stationId, targetLocal, endTime).forEach(data -> {
                SynopSnapshot snap = mapper.fromForecast(data);
                timelineMap.put(snap.timestamp(), snap);
            });
        }

        return CompletableFuture.completedFuture(new ArrayList<>(timelineMap.values()));
    }
}
