package pl.czyzlowie.modules.fish_forecast.application;

import pl.czyzlowie.modules.fish_forecast.api.dto.FishForecastRequestDto;

public interface FishForecastOrchestrator {
    void calculateFishForecast(FishForecastRequestDto request);
}
