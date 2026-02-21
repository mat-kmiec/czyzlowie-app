package pl.czyzlowie.modules.spot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.czyzlowie.modules.spot.dto.SpotDetailsDto;
import pl.czyzlowie.modules.spot.service.SpotDetailsService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SpotDetailsController {

    private final SpotDetailsService spotDetailsService;

    @GetMapping({
            "/jezioro/{slug}",
            "/rzeka/{slug}",
            "/komercja/{slug}",
            "/lowisko/{slug}",
            "/slip/{slug}"
    })
    public String showSpotDetails(@PathVariable String slug, Model model) {
        log.debug("Żądanie wyświetlenia szczegółów dla łowiska o slugu: {}", slug);

        try {
            SpotDetailsDto spotDto = spotDetailsService.getSpotDetailsBySlug(slug);
            model.addAttribute("spot", spotDto);
            model.addAttribute("type", spotDto.getSpotType().name());
            return "spot-details";

        } catch (Exception e) {
            log.warn("Nie udało się załadować szczegółów łowiska '{}': {}", slug, e.getMessage());
            return "redirect:/mapa?error=spot_not_found";
        }
    }
}
