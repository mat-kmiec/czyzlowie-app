package pl.czyzlowie.modules.fish_forecast.application;

import pl.czyzlowie.modules.fish_forecast.api.dto.FishForecastRequestDto;
import pl.czyzlowie.modules.fish_forecast.api.dto.FishForecastResponseDto;

import java.util.concurrent.CompletableFuture;

/**
 * The FishForecastOrchestrator interface defines a contract for orchestrating
 * fish forecasting operations based on a provided forecast request.
 *
 * This interface is designed to integrate multiple forecasting services,
 * including location, hydrological, meteorological, lunar, and other relevant
 * inputs, to calculate and return a detailed fish forecast response.
 *
 * The forecast calculation is performed asynchronously to allow non-blocking
 * execution. The input and output are encapsulated in Data Transfer Objects (DTOs)
 * to ensure clear separation of concerns and streamlined data interchange.
 *
 * Method Summary:
 *
 * - `calculateFishForecast`: Processes a fish forecast request and returns a
 *   CompletableFuture providing the forecast response once the asynchronous
 *   calculation is complete.
 */
public interface FishForecastOrchestrator {
    CompletableFuture<FishForecastResponseDto> calculateFishForecast(FishForecastRequestDto request);
}
