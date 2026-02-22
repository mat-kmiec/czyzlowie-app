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

@Controller
@RequestMapping("/lowiska")
@RequiredArgsConstructor
public class SpotListController {

    private final SpotListService spotListService;

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