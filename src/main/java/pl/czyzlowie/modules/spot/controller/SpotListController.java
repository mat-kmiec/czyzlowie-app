package pl.czyzlowie.modules.spot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.czyzlowie.modules.map.entity.SpotType;
import pl.czyzlowie.modules.spot.dto.SpotFilterDto;
import pl.czyzlowie.modules.spot.dto.SpotListElementDto;
import pl.czyzlowie.modules.spot.service.SpotListService;

import java.util.List;

/**
 * SpotListController is a Spring MVC controller responsible for handling requests
 * related to displaying a filtered, paginated list of spots.
 * It processes incoming requests, applies the necessary filters, and prepares
 * the data to be rendered on the "spots-list" view.
 *
 * The class uses dependency injection to utilize the SpotListService for
 * retrieving filtered spot data.
 *
 * Request mappings:
 * - "/lowiska": Displays the full list of spots.
 * - "/lowiska/{urlPath}": Displays a filtered list of spots based on the type
 *   derived from the provided URL path.
 */
@Controller
@RequestMapping("/lowiska")
@RequiredArgsConstructor
public class SpotListController {

    private final SpotListService spotListService;

    /**
     * Handles requests to retrieve and display a list of spots based on the provided filters, pagination options,
     * and optional URL path to determine the spot type.
     *
     * @param urlPath an optional path variable used to identify the type of spots to filter; if null or blank,
     *                no specific type filtering is applied
     * @param filter an object containing filtering criteria for the spots (e.g., type, properties)
     * @param pageable pageable information to handle pagination and sorting (e.g., page size, sort criteria)
     * @param model a holder for attributes that need to be passed to the view for rendering
     * @return the name of the view template (spots-list) to render the list of spots
     */
    @GetMapping({"", "/{urlPath}"})
    public String listSpots(
            @PathVariable(required = false) String urlPath,
            @ModelAttribute SpotFilterDto filter,
            @PageableDefault(size = 50, sort = "name") Pageable pageable,
            Model model) {

        String currentType = "all";
        String typeName = null;

        if (urlPath != null && !urlPath.isBlank()) {
            SpotType typeFromUrl = SpotType.fromUrlPath(urlPath);
            if (typeFromUrl != null) {
                filter.setSpotType(typeFromUrl);
            }
        }

        if (filter.getSpotType() != null) {
            currentType = filter.getSpotType().getUrlPath();
            typeName = filter.getSpotType().getDisplayName();
        }

        List<SpotType> availableTypes = java.util.Arrays.stream(SpotType.values())
                .filter(type -> type != SpotType.RESTRICTION)
                .toList();

        Page<SpotListElementDto> spotsPage = spotListService.getFilteredSpots(filter, pageable);
        model.addAttribute("spotsPage", spotsPage);
        model.addAttribute("filter", filter);
        model.addAttribute("allTypes", availableTypes);
        model.addAttribute("currentType", currentType);
        model.addAttribute("typeName", typeName);

        return "spots-list";
    }
}