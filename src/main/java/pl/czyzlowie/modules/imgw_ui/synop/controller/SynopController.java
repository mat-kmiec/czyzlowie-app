package pl.czyzlowie.modules.imgw_ui.synop.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.czyzlowie.modules.imgw_ui.synop.dto.SynopDashboardDto;
import pl.czyzlowie.modules.imgw_ui.synop.service.SynopViewService;

import java.time.LocalDate;

@Controller
@RequestMapping("/synop")
@RequiredArgsConstructor
public class SynopController {

    private final SynopViewService synopViewService;

    @GetMapping
    public String getSynopPage(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false, name = "miasto") String miasto,
            Model model) {

        String locationName = (miasto != null && !miasto.isBlank()) ? miasto : "Warszawa";
        double targetLat = (lat != null) ? lat : 52.2297;
        double targetLon = (lon != null) ? lon : 21.0122;
        LocalDate today = LocalDate.now();

        SynopDashboardDto dashboard = synopViewService.getDashboardData(targetLat, targetLon, locationName, today);

        model.addAttribute("locationName", locationName);
        model.addAttribute("dashboard", dashboard);

        return "essentials/synop";
    }
}