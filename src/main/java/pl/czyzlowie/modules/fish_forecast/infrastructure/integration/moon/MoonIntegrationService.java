package pl.czyzlowie.modules.fish_forecast.infrastructure.integration.moon;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class MoonIntegrationService {

    private final MoonGlobalDataRepository globalRepository;
    private final MoonStationDataRepository stationRepository;
    private final MoonDataMapper mapper;
    private static final int HISTORY_DAYS = 3;
    private static final int FORECAST_DAYS = 2;

    @Async("dataFetchExecutor")
    public CompletableFuture<List<MoonSnapshot>> fetchMoonTimeline(String stationId, String stationType, ZonedDateTime targetTime) {
        LocalDate targetDate = targetTime.toLocalDate();
        LocalDate startDate = targetDate.minusDays(HISTORY_DAYS);
        LocalDate endDate = targetDate.plusDays(FORECAST_DAYS);
        List<MoonGlobalData> globalData = globalRepository.findByCalculationDateBetweenOrderByCalculationDateAsc(startDate, endDate);

        List<MoonStationData> stationData = List.of();
        if (stationId != null && stationType != null) {
            stationData = stationRepository.findByIdStationIdAndIdStationTypeAndIdCalculationDateBetweenOrderByIdCalculationDateAsc(
                    stationId, stationType, startDate, endDate
            );
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
