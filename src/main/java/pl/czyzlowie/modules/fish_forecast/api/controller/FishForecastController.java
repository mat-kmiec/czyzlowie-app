package pl.czyzlowie.modules.fish_forecast.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.czyzlowie.modules.fish_forecast.api.dto.FishForecastRequestDto;
import pl.czyzlowie.modules.fish_forecast.api.dto.FishForecastResponseDto;
import pl.czyzlowie.modules.fish_forecast.application.FishForecastOrchestrator;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fish-forecast")
public class FishForecastController {

    private final FishForecastOrchestrator orchestrator;

    @GetMapping
    public CompletableFuture<FishForecastResponseDto> getFishForecast(@Valid @ModelAttribute FishForecastRequestDto request) {
        return orchestrator.calculateFishForecast(request);
    }
}
