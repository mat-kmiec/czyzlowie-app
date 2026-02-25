package pl.czyzlowie.modules.legal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LegalController {

    @GetMapping("/regulamin")
    public String terms() {
        return "info/terms";
    }

    @GetMapping("/polityka-prywatnosci")
    public String privacy() {
        return "info/privacy";
    }

    @GetMapping("/o-nas")
    public String about() {
        return "info/about";
    }
}
