package pl.czyzlowie.modules.barometer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.czyzlowie.modules.barometer.entity.StationType;
import pl.czyzlowie.modules.barometer.service.BarometerEngineService;

@Slf4j
@RestController
@RequestMapping("/api/weather/admin/barometer")
@RequiredArgsConstructor
public class BarometerAdminController {

    private final BarometerEngineService engineService;

    @PostMapping("/calculate/{stationType}/{stationId}")
    public ResponseEntity<String> triggerCalculation(
            @PathVariable StationType stationType,
            @PathVariable String stationId) {

        log.info("Otrzymano żądanie przeliczenia barometru dla: {} ({})", stationId, stationType);

        try {
            engineService.calculateAndSaveStats(stationId, stationType);
            return ResponseEntity.ok(
                    String.format("Sukces! Obliczono i zaktualizowano statystyki dla stacji: %s (%s). Sprawdź tabelę station_barometer_stats.", stationId, stationType)
            );
        } catch (Exception e) {
            log.error("Błąd podczas przeliczania statystyk", e);
            return ResponseEntity.internalServerError().body("Wystąpił błąd: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> triggerTestStation() {
        engineService.calculateAndSaveStats("TEST_01", StationType.IMGW_SYNOP);
        return ResponseEntity.ok("Wyzwolono obliczenia dla TEST_01. Odśwież widok bazy danych (tabela station_barometer_stats)!");
    }
}