package pl.czyzlowie.modules.fish_forecast.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.czyzlowie.modules.fish_forecast.api.dto.FishForecastRequestDto;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/fish-forecast/")
public class FishForecastController {

    @PostMapping
    public String getFishForecast(@Valid @RequestBody FishForecastRequestDto request) {
        return "";
    }
}
