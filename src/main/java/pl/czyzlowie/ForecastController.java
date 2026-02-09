package pl.czyzlowie;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.czyzlowie.modules.forecast.service.VirtualStationDataService;
import pl.czyzlowie.modules.forecast.service.WeatherForecastDataService;

@RestController
@RequestMapping("/api/forecast")
@RequiredArgsConstructor
public class ForecastController {

    private final WeatherForecastDataService forecastService;
    private final VirtualStationDataService virtualStationDataService;

    /**
     * Ręczne wyzwolenie aktualizacji prognoz.
     * Pobiera dane dla wszystkich aktywnych stacji IMGW oraz Wirtualnych.
     */
    @PostMapping("/update")
    public ResponseEntity<String> updateForecasts() {
        forecastService.updateAllForecasts();

        return ResponseEntity.ok("Zlecono aktualizację prognoz pogody (Open-Meteo).");
    }

    @PostMapping("/update-hour")
    public ResponseEntity<String> updateHourForecasts() {
        virtualStationDataService.fetchAndSaveCurrentData();

        return ResponseEntity.ok("Zlecono aktualizację prognozy godzinowej dla stacji wirtualnych pogody (Open-Meteo).");
    }
}