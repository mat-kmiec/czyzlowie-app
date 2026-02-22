package pl.czyzlowie.modules.spot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.czyzlowie.modules.map.entity.SpotType;
import pl.czyzlowie.modules.spot.dto.SpotDetailsDto;
import pl.czyzlowie.modules.spot.service.SpotDetailsService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SpotDetailsController {

    private final SpotDetailsService spotDetailsService;

    @GetMapping("/jezioro/{slug}")
    public String showLakeDetails(@PathVariable String slug, Model model) {
        return processSpotRequest(slug, SpotType.LAKE, model);
    }

    @GetMapping("/rzeka/{slug}")
    public String showRiverDetails(@PathVariable String slug, Model model) {
        return processSpotRequest(slug, SpotType.RIVER, model);
    }

    @GetMapping("/starorzecze/{slug}")
    public String showOxbowDetails(@PathVariable String slug, Model model) {
        return processSpotRequest(slug, SpotType.OXBOW, model);
    }

    @GetMapping("/zbiornik-zaporowy/{slug}")
    public String showReservoirDetails(@PathVariable String slug, Model model) {
        return processSpotRequest(slug, SpotType.RESERVOIR, model);
    }

    @GetMapping("/miejsce-wodowania/{slug}")
    public String showSlipDetails(@PathVariable String slug, Model model) {
        return processSpotRequest(slug, SpotType.SLIP, model);
    }

    @GetMapping("/miejscowka/{slug}")
    public String showSpecificSpotDetails(@PathVariable String slug, Model model) {
        return processSpotRequest(slug, SpotType.SPECIFIC_SPOT, model);
    }

    @GetMapping("/lowisko-komercyjne/{slug}")
    public String showCommercialDetails(@PathVariable String slug, Model model) {
        return processSpotRequest(slug, SpotType.COMMERCIAL, model);
    }

    private String processSpotRequest(String slug, SpotType type, Model model) {
        try {
            SpotDetailsDto spotDto = spotDetailsService.getSpotDetailsBySlugAndType(slug, type);
            model.addAttribute("spot", spotDto);
            model.addAttribute("type", spotDto.getSpotType().name());
            return "spot-details";
        } catch (Exception e) {
            log.warn("Błąd dla sluga {}: {}", slug, e.getMessage());
            return "redirect:/mapa?error=spot_not_found";
        }
    }


}
