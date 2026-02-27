package pl.czyzlowie.modules.fish_forecast.api.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.List;

public record FishForecastRequestDto(

    @NotNull(message = "Szerokość geograficzna jest wymagana")
    @Min(value = -90, message = "Nieprawidłowa szerokość geograficzna")
    @Max(value = 90, message = "Nieprawidłowa szerokość geograficzna")
    Double lat,

    @NotNull(message = "Długość geograficzna jest wymagana")
    @Min(value = -180, message = "Nieprawidłowa długość geograficzna")
    @Max(value = 180, message = "Nieprawidłowa długość geograficzna")
    Double lon,

    @NotNull(message = "Lokalizacja jest wymagana")
    String location,

    @NotNull(message = "Czas jest wymagany")
    @FutureOrPresent(message = "Prognoza musi dotyczyć teraźniejszości lub przyszłości")
    ZonedDateTime targetTime,

    List<Integer> targetFishSpeciesIds,

    @NotNull(message = "Musisz określić, czy ignorować dane hydro")
    Boolean ignoreHydro,

    @NotNull(message = "Musisz określić, czy ignorować dane meteo")
    Boolean ignoreMeteo




) {}
