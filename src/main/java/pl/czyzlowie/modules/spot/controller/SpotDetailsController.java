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

/**
 * SpotDetailsController is a Spring MVC controller responsible for handling HTTP GET requests
 * related to various types of spots (e.g., lakes, rivers, oxbows, reservoirs, commercial fishing spots, etc.).
 * It provides methods for fetching and displaying spot details based on unique identifiers (slugs) and spot types.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class SpotDetailsController {

    private final SpotDetailsService spotDetailsService;

    /**
     * Handles requests to display details of a specific lake-related spot.
     * Retrieves the lake spot details based on the provided slug and populates the model
     * with the relevant data.
     *
     * @param slug the unique identifier for the lake spot
     * @param model the model object used to pass data to the view
     * @return the name of the view to be rendered
     */
    @GetMapping("/jezioro/{slug}")
    public String showLakeDetails(@PathVariable String slug, Model model) {
        return processSpotRequest(slug, SpotType.LAKE, model);
    }

    /**
     * Handles requests to display details of a specific river-related spot.
     * Retrieves the river spot details based on the provided slug and populates the model
     * with the relevant data.
     *
     * @param slug the unique identifier for the river spot
     * @param model the model object used to pass data to the view
     * @return the name of the view to be rendered
     */
    @GetMapping("/rzeka/{slug}")
    public String showRiverDetails(@PathVariable String slug, Model model) {
        return processSpotRequest(slug, SpotType.RIVER, model);
    }

    /**
     * Handles requests to display details of a specific oxbow-related spot.
     * Retrieves the oxbow spot details based on the provided slug and populates the model
     * with the relevant data.
     *
     * @param slug the unique identifier for the oxbow spot
     * @param model the model object used to pass data to the view
     * @return the name of the view to be rendered
     */
    @GetMapping("/starorzecze/{slug}")
    public String showOxbowDetails(@PathVariable String slug, Model model) {
        return processSpotRequest(slug, SpotType.OXBOW, model);
    }

    @GetMapping("/zbiornik-zaporowy/{slug}")
    public String showReservoirDetails(@PathVariable String slug, Model model) {
        return processSpotRequest(slug, SpotType.RESERVOIR, model);
    }

    /**
     * Handles requests to display the details of a slip spot.
     * Fetches the details of the spot using the provided slug, assigns the data to the model,
     * and returns the name of the view to be rendered.
     *
     * @param slug the unique identifier for the slip spot
     * @param model the model object used to pass data to the view
     * @return the name of the view to be rendered
     */
    @GetMapping("/slip/{slug}")
    public String showSlipDetails(@PathVariable String slug, Model model) {
        return processSpotRequest(slug, SpotType.SLIP, model);
    }

    /**
     * Handles the HTTP GET request for the details of a specific spot identified by its slug.
     * Retrieves the details of the specific spot and processes the request.
     *
     * @param slug the unique identifier (slug) for the specific spot
     * @param model the model to add attributes to for use in the view
     * @return the name of the view to be rendered containing the spot details
     */
    @GetMapping("/miejscowka/{slug}")
    public String showSpecificSpotDetails(@PathVariable String slug, Model model) {
        return processSpotRequest(slug, SpotType.SPECIFIC_SPOT, model);
    }

    /**
     * Handles requests to display details of a commercial fishing spot.
     *
     * @param slug the unique identifier for the fishing spot
     * @param model the model object used to pass data to the view
     * @return the name of the view to be rendered
     */
    @GetMapping("/lowisko-komercyjne/{slug}")
    public String showCommercialDetails(@PathVariable String slug, Model model) {
        return processSpotRequest(slug, SpotType.COMMERCIAL, model);
    }

    /**
     * Processes a request to retrieve and display details of a specific spot.
     * Retrieves the spot details based on the provided slug and spot type,
     * and populates the model with the relevant data. In case of an error,
     * redirects the user to a fallback URL.
     *
     * @param slug The unique identifier for the specific spot.
     * @param type The type of the spot (e.g., lake, river, etc.).
     * @param model The model object to which the spot details and type are added as attributes.
     * @return The name of the view to be rendered, such as "spot-details" when the operation is successful,
     *         or a redirect URL in case of an error.
     */
    private String processSpotRequest(String slug, SpotType type, Model model) {
        try {
            SpotDetailsDto spotDto = spotDetailsService.getSpotDetailsBySlugAndType(slug, type);
            model.addAttribute("spot", spotDto);
            model.addAttribute("type", spotDto.getSpotType().name());
            return "map/spot-details";
        } catch (Exception e) {
            log.warn("Błąd dla sluga {}: {}", slug, e.getMessage());
            return "redirect:/mapa?error=spot_not_found";
        }
    }


}
