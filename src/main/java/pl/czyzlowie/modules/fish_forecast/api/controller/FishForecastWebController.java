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
    // UWAGA: Usunięto @ResponseBody!
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

                    // Bezpieczne wstrzyknięcie JSON-ów z wykresem (zostawiamy na przyszłość)
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

    // ZMIANA: Zwracamy CompletableFuture<ModelAndView> i usuwamy 'Model model' z argumentów
//    @PostMapping("/wynik")
//    @org.springframework.web.bind.annotation.ResponseBody // Zwracamy czysty tekst, omijamy błędy HTML
//    public CompletableFuture<String> calculateAndShowResult(@ModelAttribute FishForecastRequestDto request) {
//
//        return orchestrator.calculateFishForecast(request)
//                .thenApply(responseDto -> {
//                    GlobalForecastResult report = responseDto.forecastResult();
//
//                    log.info("\n\n=========================================================================");
//                    log.info("🐟 RAPORT TAKTYCZNY NASA - GŁÓWNE OBLICZENIA 🐟");
//                    log.info("Lokalizacja: {}", request.location());
//                    log.info("Czas skanowania: {}", request.targetTime());
//                    log.info("Ogólny Index Brań: {}%", report.generalBiteIndex());
//                    log.info("-------------------------------------------------------------------------");
//
//                    if (report.speciesReports() != null && !report.speciesReports().isEmpty()) {
//                        report.speciesReports().forEach(species -> {
//                            log.info(">> GATUNEK: {} | SCORE: {}%", species.speciesName(), species.totalScore());
//                            log.info("   Z-Axis: {} | Balistyka: {}", species.zAxisLocation(), species.tackleBallistics());
//                            if (species.combinedTips() != null) {
//                                species.combinedTips().forEach(tip -> log.info("   * {}", tip));
//                            }
//                        });
//                    }
//
//                    log.info("\n=========================================================================");
//                    log.info("📊 ZRZUT CAŁEJ TELEMETRII (WEATHER CONTEXT) 📊");
//
//                    // 1. OŚ SYNOP (Atmosfera)
//                    log.info(">>> SYNOP TIMELINE (Wielkość: {}) <<<", report.fullContext().synopTimeline().size());
//                    report.fullContext().synopTimeline().forEach(s ->
//                            log.info("    [{}] Temp: {}°C | Ciśnienie: {} hPa | Wiatr: {} km/h | Chmury: {}%",
//                                    s.timestamp(), s.temperature(), s.pressure(), s.windSpeed(), s.cloudCover())
//                    );
//
//                    // 2. OŚ HYDRO (Woda)
//                    log.info("\n>>> HYDRO TIMELINE (Wielkość: {}) <<<", report.fullContext().hydroTimeline().size());
//                    report.fullContext().hydroTimeline().forEach(h ->
//                            log.info("    [{}] Temp. Wody: {}°C | Stan: {} cm | Przepływ: {} m³/s",
//                                    h.timestamp(), h.waterTemperature(), h.waterLevel(), h.discharge())
//                    );
//
//                    // 3. OŚ METEO (Mikroklimat / Deszcz)
//                    log.info("\n>>> METEO TIMELINE (Wielkość: {}) <<<", report.fullContext().meteoTimeline().size());
//                    report.fullContext().meteoTimeline().forEach(m ->
//                            log.info("    [{}] Temp(Air): {}°C | Temp(Grunt): {}°C | Opad(10m): {} mm",
//                                    m.timestamp(), m.airTemperature(), m.groundTemperature(), m.precipitation10min())
//                    );
//
//                    // 4. OŚ MOON (Księżyc / Słońce)
//                    log.info("\n>>> MOON & SUN TIMELINE (Wielkość: {}) <<<", report.fullContext().moonTimeline().size());
//                    report.fullContext().moonTimeline().forEach(moon ->
//                            log.info("    [{}] Faza: {} | Ilum: {}% | Tranzyt: {} | Świt: {} | Zmierzch: {}",
//                                    moon.date(), moon.phaseName(), moon.illuminationPct(),
//                                    moon.transit() != null ? moon.transit().toLocalTime() : "Brak",
//                                    moon.sunrise() != null ? moon.sunrise().toLocalTime() : "Brak",
//                                    moon.sunset() != null ? moon.sunset().toLocalTime() : "Brak")
//                    );
//
//                    log.info("=========================================================================\n");
//
//                    return "SUKCES! Przejdź do konsoli w IntelliJ, aby zobaczyć absolutnie wszystkie dane, które zgromadził silnik.";
//                })
//                .exceptionally(ex -> {
//                    log.error("KRYTYCZNY BŁĄD W TLE OBLICZEŃ: ", ex);
//                    return "BŁĄD: " + ex.getMessage();
//                });
//    }
}