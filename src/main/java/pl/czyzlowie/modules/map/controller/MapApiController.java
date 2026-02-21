package pl.czyzlowie.modules.map.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.czyzlowie.modules.map.dto.MapMarkerDto;
import pl.czyzlowie.modules.map.service.MapMarkerService;

import java.util.List;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class MapApiController {

    private final MapMarkerService mapMarkerService;

    @GetMapping("/markers")
    public List<MapMarkerDto> getMarkers(
            @RequestParam(required = false) Double north,
            @RequestParam(required = false) Double south,
            @RequestParam(required = false) Double east,
            @RequestParam(required = false) Double west
    ) {
        if (north != null && south != null && east != null && west != null) {
            return mapMarkerService.getMarkersInBounds(north, south, east, west);
        }

        return mapMarkerService.getAllMarkers();
    }
}
