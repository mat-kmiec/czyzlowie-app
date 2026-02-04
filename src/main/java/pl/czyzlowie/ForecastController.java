package pl.czyzlowie;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.czyzlowie.modules.forecast.service.WeatherForecastService;

@RestController
@RequestMapping("/api/forecast")
@RequiredArgsConstructor
public class ForecastController {

    private final WeatherForecastService forecastService;

    /**
     * Ręczne wyzwolenie aktualizacji prognoz.
     * Pobiera dane dla wszystkich aktywnych stacji IMGW oraz Wirtualnych.
     */
    @PostMapping("/update")
    public ResponseEntity<String> updateForecasts() {
        // Ta metoda uruchamia logikę "Smart Update" (Batch) dla wszystkich stacji
        forecastService.updateAllForecasts();

        return ResponseEntity.ok("Zlecono aktualizację prognoz pogody (Open-Meteo).");
    }
}