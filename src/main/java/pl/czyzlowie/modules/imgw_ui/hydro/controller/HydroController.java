package pl.czyzlowie.modules.imgw_ui.hydro.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.czyzlowie.modules.imgw_ui.hydro.dto.HydroDashboardDto;
import pl.czyzlowie.modules.imgw_ui.hydro.service.HydroViewService;

@Controller
@RequestMapping("/hydro")
@RequiredArgsConstructor
public class HydroController {

    private final HydroViewService hydroViewService;

    @GetMapping
    public String getHydroPage(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) String miasto,
            @RequestParam(required = false) String lokalizacja,
            Model model) {

        String inputName = (lokalizacja != null && !lokalizacja.isBlank()) ? lokalizacja : miasto;

        String locationName = (inputName != null && !inputName.isBlank()) ? inputName : "Warszawa";

        double targetLat = (lat != null) ? lat : 52.2297;
        double targetLon = (lon != null) ? lon : 21.0122;

        HydroDashboardDto dashboard = hydroViewService.getDashboardData(targetLat, targetLon, locationName);

        model.addAttribute("dashboard", dashboard);
        model.addAttribute("lokalizacja", locationName);

        return "essentials/hydro";
    }
}
