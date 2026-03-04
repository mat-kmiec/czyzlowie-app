package pl.czyzlowie.modules.fish_forecast.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * A Data Transfer Object (DTO) representing the request for a fish forecasting operation.
 * This class is used to encapsulate all necessary input parameters for performing a forecast
 * operation, including geographical data, time, and optional control flags.
 *
 * Fields:
 *
 * - `lat`: Latitude of the location for the forecast, ranging from -90 to 90. This field is required.
 * - `lon`: Longitude of the location for the forecast, ranging from -180 to 180. This field is required.
 * - `targetTime`: The date and time for which the forecast is requested. This field is required.
 * - `location`: A description or identifier for the forecast location. This field is required.
 * - `targetFishSpeciesIds`: A list of fish species ID filters to target specific species in the forecast. Defaults to an empty list if not provided.
 * - `ignoreHydro`: A boolean flag indicating whether hydrological conditions should be ignored in the forecast. Defaults to `false` if not provided.
 * - `ignoreMeteo`: A boolean flag indicating whether meteorological conditions should be ignored in the forecast. Defaults to `false` if not provided.
 *
 * The class contains validation annotations to ensure proper input data is provided, such as constraints
 * for latitude, longitude, and required fields. Additionally, default values are assigned to optional fields
 * during object initialization if not explicitly provided.
 */
public record FishForecastRequestDto(

        @NotNull(message = "Szerokość geograficzna jest wymagana")
        @Min(value = -90, message = "Nieprawidłowa szerokość geograficzna")
        @Max(value = 90, message = "Nieprawidłowa szerokość geograficzna")
        Double lat,

        @NotNull(message = "Długość geograficzna jest wymagana")
        @Min(value = -180, message = "Nieprawidłowa długość geograficzna")
        @Max(value = 180, message = "Nieprawidłowa długość geograficzna")
        Double lon,

        @NotNull(message = "Czas jest wymagany")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        ZonedDateTime targetTime,

        @NotNull(message = "Lokalizacja jest wymagana")
        String location,

        List<Integer> targetFishSpeciesIds,
        Boolean ignoreHydro,
        Boolean ignoreMeteo

) {

    public FishForecastRequestDto {
        if (ignoreHydro == null) ignoreHydro = false;
        if (ignoreMeteo == null) ignoreMeteo = false;
        if (targetFishSpeciesIds == null) targetFishSpeciesIds = java.util.List.of();
    }
}
