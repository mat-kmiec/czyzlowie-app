package pl.czyzlowie.modules.barometer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.barometer.entity.StationType;
import pl.czyzlowie.modules.forecast.entity.VirtualStation;
import pl.czyzlowie.modules.forecast.repository.VirtualStationRepository;
import pl.czyzlowie.modules.imgw.entity.ImgwSynopStation;
import pl.czyzlowie.modules.imgw.repository.ImgwSynopStationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class BarometerSchedulerService {

    private final ImgwSynopStationRepository synopRepository;
    private final VirtualStationRepository virtualRepository;
    private final BarometerEngineService engineService;
    private final Executor executor;

    public BarometerSchedulerService(
            ImgwSynopStationRepository synopRepository,
            VirtualStationRepository virtualRepository,
            BarometerEngineService engineService,
            @Qualifier("weatherExecutor") Executor executor) {
        this.synopRepository = synopRepository;
        this.virtualRepository = virtualRepository;
        this.engineService = engineService;
        this.executor = executor;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "0 0/30 * * * *")
    public void calculateAllBarometerStatsAutomated() {
        log.info("[BAROMETR-JOB] START: Asynchroniczne przeliczanie statystyk...");

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        List<ImgwSynopStation> synopStations = synopRepository.findAllByIsActiveTrue();
        List<VirtualStation> virtualStations = virtualRepository.findAllByActiveTrue();

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (ImgwSynopStation station : synopStations) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    engineService.calculateAndSaveStats(station.getId(), StationType.IMGW_SYNOP);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    log.error("[BAROMETR-JOB] Błąd stacji SYNOP {}: {}", station.getId(), e.getMessage());
                    errorCount.incrementAndGet();
                }
            }, executor);
            futures.add(future);
        }

        for (VirtualStation station : virtualStations) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    engineService.calculateAndSaveStats(station.getId(), StationType.VIRTUAL);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    log.error("[BAROMETR-JOB] Błąd stacji VIRTUAL {}: {}", station.getId(), e.getMessage());
                    errorCount.incrementAndGet();
                }
            }, executor);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        log.info("[BAROMETR-JOB] KONIEC: Przeliczono pomyślnie: {} stacji. Błędy: {}",
                successCount.get(), errorCount.get());
    }
}