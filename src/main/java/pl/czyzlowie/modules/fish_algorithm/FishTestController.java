package pl.czyzlowie.modules.fish_algorithm;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/prognoza")
public class FishTestController {


    @GetMapping
    public String test() {
        return "fish-algorithm/alg";
    }
}
