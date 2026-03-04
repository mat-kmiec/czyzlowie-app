package pl.czyzlowie.modules.fish_forecast.api.dto;

import pl.czyzlowie.modules.fish_forecast.domain.engine.GlobalForecastResult;

/**
 * A Data Transfer Object (DTO) representing the response for a fish forecasting request.
 * This class contains information about the status of the forecasting operation,
 * an optional message, and a detailed global forecast result.
 *
 * Fields:
 *
 * - `status`: A string that indicates the status of the forecast operation.
 * - `message`: A string containing additional information or context regarding the forecast operation.
 * - `forecastResult`: An instance of {@link GlobalForecastResult} that encapsulates the detailed forecast data,
 *                     including weather, hydrological, and lunar predictions, as well as calculated indices
 *                     such as the general bite index and species-specific reports.
 */
public record FishForecastResponseDto(
        String status,
        String message,
        GlobalForecastResult forecastResult
) {}