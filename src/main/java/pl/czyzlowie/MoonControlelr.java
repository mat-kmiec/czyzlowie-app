package pl.czyzlowie;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.czyzlowie.modules.moon.service.MoonGlobalBatchService;
import pl.czyzlowie.modules.moon.service.MoonStationBatchService;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/moon")
@RequiredArgsConstructor
@Slf4j
public class MoonControlelr {

    private final MoonGlobalBatchService moonGlobalBatchService;
    private final MoonStationBatchService moonStationBatchService;

    @PostMapping("/global")
    public ResponseEntity<String> updateMoon() {
        moonGlobalBatchService.generateGlobalDataForYear(2026);
        return ResponseEntity.ok("Zlecono obliczenie danych MOON.");
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateMoonData(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Otrzymano żądanie API wygenerowania danych księżyca od {} do {}", startDate, endDate);

        // Prosta walidacja
        if (endDate.isBefore(startDate)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Data końcowa nie może być przed datą początkową."));
        }

        try {
            // 1. Najpierw generujemy dane globalne (fazy, odległość itp.)
            moonGlobalBatchService.generateGlobalDataForDateRange(startDate, endDate);

            // 2. Następnie generujemy dane dla wszystkich aktywnych stacji (wschody, zachody itp.)
            moonStationBatchService.generateStationDataForDateRange(startDate, endDate);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", String.format("Pomyślnie zakończono proces generowania danych od %s do %s.", startDate, endDate)
            ));

        } catch (Exception e) {
            log.error("Błąd podczas generowania danych księżycowych z API", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "Wystąpił błąd serwera podczas generowania danych: " + e.getMessage()
            ));
        }
    }
}
