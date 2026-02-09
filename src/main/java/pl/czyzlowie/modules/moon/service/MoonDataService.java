package pl.czyzlowie.modules.moon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.moon.client.AstronomyClient;
import pl.czyzlowie.modules.moon.client.dto.AstronomyResponse;
import pl.czyzlowie.modules.moon.entity.MoonData;
import pl.czyzlowie.modules.moon.entity.MoonRegion;
import pl.czyzlowie.modules.moon.mapper.MoonDataMapper;
import pl.czyzlowie.modules.moon.repository.MoonDataRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
@Slf4j
public class MoonDataService {

    private static final int FORECAST_DAYS = 8;

    private final MoonDataRepository repository;
    private final AstronomyClient client;
    private final MoonDataMapper mapper;

    private final Executor moonExecutor;

    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "0 0 2 * * ?")
    public void ensureMoonData() {
        log.info("START: Aktualizacja danych księżycowych (Async)...");
        LocalDate today = LocalDate.now();

        for (int i = 0; i < FORECAST_DAYS; i++) {
            LocalDate targetDate = today.plusDays(i);
            processDayAsync(targetDate);
        }

        log.info("KONIEC: Zadania zlecone.");
    }

    private void processDayAsync(LocalDate date) {
        List<CompletableFuture<MoonData>> futures = new ArrayList<>();

        for (MoonRegion region : MoonRegion.values()) {
            if (repository.existsByDateAndRegionNode(date, region)) {
                continue;
            }

            CompletableFuture<MoonData> future = CompletableFuture.supplyAsync(() -> {
                        log.debug("Fetching Moon -> {} [{}]", region, date);
                        AstronomyResponse response = client.fetchMoonData(region.getLatitude(), region.getLongitude(), date);
                        return mapper.toEntity(response, region, date);
                    }, moonExecutor)
                    .exceptionally(ex -> {
                        log.error("Błąd pobierania dla {}: {}", region, ex.getMessage());
                        return null;
                    });

            futures.add(future);
        }

        if (!futures.isEmpty()) {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenAccept(v -> {
                        List<MoonData> results = futures.stream()
                                .map(CompletableFuture::join)
                                .filter(Objects::nonNull)
                                .toList();

                        if (!results.isEmpty()) {
                            repository.saveAll(results);
                            log.info("Zapisano {} rekordów księżycowych na dzień {}", results.size(), date);
                        }
                    });
        }
    }
}