package pl.czyzlowie.modules.fish.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import pl.czyzlowie.modules.fish.dto.FishDetailsDto;
import pl.czyzlowie.modules.fish.dto.FishFilterDto;
import pl.czyzlowie.modules.fish.dto.FishListElementDto;
import pl.czyzlowie.modules.fish.entity.enums.FishCategory;
import pl.czyzlowie.modules.fish.service.FishSpeciesService;

/**
 * Web Controller for the Fish Atlas module, handling requests related to fish species browsing.
 *
 * This controller manages the presentation logic for both the paginated list of fish
 * (with filtering support) and the detailed profiles of individual species. It acts
 * as the bridge between the FishSpeciesService and the Thymeleaf view layer.
 *
 * Key responsibilities include:
 * - URL Mapping: Handling SEO-friendly slugs for categories and specific fish.
 * - Filtering and Pagination: Processing user search input and page navigation settings.
 * - View Model Preparation: Populating the UI model with DTOs, category metadata,
 * and current navigation paths for breadcrumbs or active links.
 * - Error Handling: Throwing appropriate response status exceptions for unknown
 * categories or missing species.
 */
@Controller
@RequestMapping("/ryby")
@RequiredArgsConstructor
public class FishAtlasController {

    private final FishSpeciesService fishSpeciesService;

    /**
     * Displays a paginated list of fish species.
     * Supports optional category filtering via URL path and custom search criteria via query parameters.
     */
    @GetMapping({"", "/kategoria/{categorySlug}"})
    public String listFish(
            @PathVariable(required = false) String categorySlug,
            @ModelAttribute FishFilterDto filter,
            @PageableDefault(size = 12, sort = "name") Pageable pageable,
            Model model) {

        if (categorySlug != null && !categorySlug.isBlank()) {
            if (categorySlug.equals("drapiezne")) {
                filter.setCategory(FishCategory.PREDATOR);
            } else if (categorySlug.equals("bialoryb")) {
                filter.setCategory(FishCategory.PEACEFUL);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nieznana kategoria ryb");
            }
        }

        String currentCategory = filter.getCategory() != null ? filter.getCategory().name() : "ALL";
        String categoryName = filter.getCategory() != null ? filter.getCategory().getDisplayName() : "Wszystkie ryby";
        String currentUrlPath = categorySlug != null ? "/kategoria/" + categorySlug : "";

        Page<FishListElementDto> fishPage = fishSpeciesService.getFilteredFish(filter, pageable);

        model.addAttribute("fishPage", fishPage);
        model.addAttribute("filter", filter);
        model.addAttribute("categories", FishCategory.values());
        model.addAttribute("currentCategory", currentCategory);
        model.addAttribute("categoryName", categoryName);
        model.addAttribute("currentUrlPath", currentUrlPath);

        return "fish/fish-list";
    }


    /**
     * Displays the full profile of a single fish species identified by its unique slug.
     */
    @GetMapping("/{slug}")
    public String fishDetails(@PathVariable String slug, Model model) {
        FishDetailsDto fishDto = fishSpeciesService.getFishDetailsDto(slug);

        model.addAttribute("fish", fishDto);

        return "fish/fish-details";
    }
}
