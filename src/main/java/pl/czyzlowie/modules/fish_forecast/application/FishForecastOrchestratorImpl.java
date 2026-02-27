package pl.czyzlowie.modules.fish_forecast.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.fish_forecast.api.dto.FishForecastRequestDto;
import pl.czyzlowie.modules.fish_forecast.infrastructure.integration.location.LocationIntegrationService;
import pl.czyzlowie.modules.fish_forecast.infrastructure.integration.location.NearestStations;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class FishForecastOrchestratorImpl implements FishForecastOrchestrator{

    private final LocationIntegrationService locationIntegrationService;

    @Override
    public void calculateFishForecast(FishForecastRequestDto req) {

        CompletableFuture<NearestStations> stationsFuture = locationIntegrationService.findNearestStations(req.lat(), req.lon(), req.ignoreHydro());
        // 1. Pobieramy dane z bazy danych
        // 2. Przygotowuje dane dla silnika
        // 3. Wykonujemy obliczenia
        // 4. Mapowanie i zwrot
    }

}
