package pl.czyzlowie.modules.legal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * LegalController handles HTTP requests related to legal information
 * and provides mappings to legal-related pages such as terms of service,
 * privacy policy, and information about the organization.
 */
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
