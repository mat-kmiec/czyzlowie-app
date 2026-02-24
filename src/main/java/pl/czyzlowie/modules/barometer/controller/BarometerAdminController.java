    package pl.czyzlowie.modules.barometer.controller;

    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import pl.czyzlowie.modules.barometer.entity.StationType;
    import pl.czyzlowie.modules.barometer.service.BarometerEngineService;

    /**
     * This controller manages administrative operations for barometer statistics,
     * including triggering calculations for specified weather stations.
     */
    @Slf4j
    @RestController
    @RequestMapping("/api/weather/admin/barometer")
    @RequiredArgsConstructor
    public class BarometerAdminController {

        private final BarometerEngineService engineService;

        /**
         * Triggers the calculation and update of barometer statistics for the specified weather station.
         *
         * @param stationType the type of the weather station (e.g., IMGW_SYNOP, VIRTUAL)
         * @param stationId   the unique identifier of the weather station
         * @return a {@code ResponseEntity} containing a success message if the calculation was successful,
         *         or an error message if an exception occurred
         */
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

        /**
         * Triggers the calculation and storage of barometer statistics for a test weather station with pre-defined parameters.
         *
         * @return a {@code ResponseEntity} containing a success message indicating that the calculation
         *         for the test station has been triggered, with a note to refresh the database view.
         */
        @GetMapping("/test")
        public ResponseEntity<String> triggerTestStation() {
            engineService.calculateAndSaveStats("TEST_01", StationType.IMGW_SYNOP);
            return ResponseEntity.ok("Wyzwolono obliczenia dla TEST_01. Odśwież widok bazy danych (tabela station_barometer_stats)!");
        }
    }