package pl.czyzlowie.modules.barometer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.czyzlowie.modules.barometer.service.BarometerViewService;

@Controller
@RequiredArgsConstructor
public class BarometerWebController {

    private final BarometerViewService viewService;

    @GetMapping("/barometr")
    public String showBarometerPage(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) String name,
            Model model) {

        model.addAttribute("barometer", viewService.getBarometerDataForView(lat, lon, name));

        return "essentials/barometr";
    }
}
