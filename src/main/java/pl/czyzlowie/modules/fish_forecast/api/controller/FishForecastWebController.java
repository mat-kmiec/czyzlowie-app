package pl.czyzlowie.modules.fish_forecast.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView; // <--- DODANY IMPORT
import pl.czyzlowie.modules.fish.repository.FishSpeciesRepository;
import pl.czyzlowie.modules.fish_forecast.api.dto.FishForecastRequestDto;
import pl.czyzlowie.modules.fish_forecast.api.dto.FishSelectionDto;
import pl.czyzlowie.modules.fish_forecast.application.FishForecastOrchestrator;
import pl.czyzlowie.modules.fish_forecast.domain.engine.GlobalForecastResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
@RequiredArgsConstructor
@RequestMapping("/prognoza")
@Slf4j
public class FishForecastWebController {

    private final FishForecastOrchestrator orchestrator;
    private final ObjectMapper objectMapper;
    private final FishSpeciesRepository fishSpeciesRepository;

    @GetMapping
    public String showKalibrator(Model model) {
        List<FishSelectionDto> allFish = fishSpeciesRepository.findAll().stream()
                .map(f -> new FishSelectionDto(f.getId(), f.getName(), f.getCategory()))
                .toList();

        model.addAttribute("allFish", allFish);
        return "fish-algorithm/kalibrator";
    }

    @PostMapping("/wynik")
    public CompletableFuture<ModelAndView> calculateAndShowResult(@Valid @ModelAttribute FishForecastRequestDto request, org.springframework.validation.BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.error("Błędy formularza: {}", bindingResult.getAllErrors());
        }

        return orchestrator.calculateFishForecast(request)
                .thenApply(responseDto -> {
                    ModelAndView mav = new ModelAndView("fish-algorithm/wynik");
                    GlobalForecastResult report = responseDto.forecastResult();

                    mav.addObject("report", report);
                    mav.addObject("locationName", request.location());

                    try {
                        mav.addObject("pressureChartJson", objectMapper.writeValueAsString(report.pressureChart()));
                        mav.addObject("tempChartJson", objectMapper.writeValueAsString(report.airTempChart()));
                    } catch (Exception e) {
                        mav.addObject("pressureChartJson", "[]");
                        mav.addObject("tempChartJson", "[]");
                    }

                    return mav;
                })
                .exceptionally(ex -> {
                    log.error("BŁĄD OBLICZEŃ: ", ex);
                    ModelAndView err = new ModelAndView("error");
                    err.addObject("message", "Błąd: " + ex.getMessage());
                    return err;
                });
    }


}