package pl.czyzlowie;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/mapa")
    public String map(){
        return "map";
    }

    @GetMapping("/ryby/*")
    public String atlas(){
        return "atlas-details";
    }
}
