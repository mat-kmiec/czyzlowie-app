package pl.czyzlowie.modules.map.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mapa")
public class MapController {

    @RequestMapping
    public String showInteractive(){
        return "map/map";
    }
}
