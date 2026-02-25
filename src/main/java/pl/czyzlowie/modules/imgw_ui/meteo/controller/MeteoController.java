package pl.czyzlowie.modules.imgw_ui.meteo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.czyzlowie.modules.imgw_ui.meteo.dto.MeteoDashboardDto;
import pl.czyzlowie.modules.imgw_ui.meteo.service.MeteoViewService;

@Controller
@RequestMapping("/meteo")
@RequiredArgsConstructor
public class MeteoController {

    private final MeteoViewService meteoViewService;

    @GetMapping
    public String getMeteoPage(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) String miasto,
            @RequestParam(required = false) String lokalizacja,
            Model model) {

        String inputName = (lokalizacja != null && !lokalizacja.isBlank()) ? lokalizacja : miasto;
        String locationName = (inputName != null && !inputName.isBlank()) ? inputName : "Warszawa";
        double targetLat = (lat != null) ? lat : 52.2297;
        double targetLon = (lon != null) ? lon : 21.0122;

        MeteoDashboardDto dashboard = meteoViewService.getDashboardData(targetLat, targetLon, locationName);

        model.addAttribute("dashboard", dashboard);
        model.addAttribute("lokalizacja", locationName);

        return "essentials/meteo";
    }
}