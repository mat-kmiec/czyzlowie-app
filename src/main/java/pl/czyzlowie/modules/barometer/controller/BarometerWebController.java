package pl.czyzlowie.modules.barometer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.czyzlowie.modules.barometer.service.BarometerViewService;

/**
 * A controller responsible for handling requests related to the barometer view.
 */
@Controller
@RequiredArgsConstructor
public class BarometerWebController {

    private final BarometerViewService viewService;

    /**
     * Handles the HTTP GET request for the barometer page.
     *
     * @param lat the latitude coordinate, can be null if not provided
     * @param lon the longitude coordinate, can be null if not provided
     * @param name the name associated with the location, can be null if not provided
     * @param model the model object used to pass attributes to the view
     * @return the name of the view to be rendered for the barometer page
     */
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
