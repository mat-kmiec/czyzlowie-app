package pl.czyzlowie.modules.map.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.czyzlowie.modules.map.dto.MapMarkerDto;
import pl.czyzlowie.modules.map.service.MapMarkerService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class MapApiController {

    private final MapMarkerService mapMarkerService;

    @GetMapping("/markers")
    public List<MapMarkerDto> getMarkers(
            @RequestParam(required = false) @Min(-90) @Max(90) Double north,
            @RequestParam(required = false) @Min(-90) @Max(90) Double south,
            @RequestParam(required = false) @Min(-180) @Max(180) Double east,
            @RequestParam(required = false) @Min(-180) @Max(180) Double west
    ) {
        if (north != null && south != null && east != null && west != null) {
            return mapMarkerService.getMarkersInBounds(north, south, east, west);
        }

        return mapMarkerService.getAllMarkers();
    }
}