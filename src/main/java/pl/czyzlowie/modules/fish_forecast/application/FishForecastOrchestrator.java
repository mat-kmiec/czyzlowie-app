package pl.czyzlowie.modules.fish_forecast.application;

import pl.czyzlowie.modules.fish_forecast.api.dto.FishForecastRequestDto;
import pl.czyzlowie.modules.fish_forecast.api.dto.FishForecastResponseDto;

import java.util.concurrent.CompletableFuture;

public interface FishForecastOrchestrator {
    CompletableFuture<FishForecastResponseDto> calculateFishForecast(FishForecastRequestDto request);
}
