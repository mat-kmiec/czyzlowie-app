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

    @GetMapping("/drapiezniki")
    public String predators(){
        return "predators";
    }

    @GetMapping("/login")
    public String login(){
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(){
        return "auth/register";
    }

    @GetMapping("/forgot-password")
    public String forgotPass(){
        return "auth/forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPass(){
        return "auth/reset-password";
    }

    @GetMapping("/error")
    public String err(){
        return "error";
    }

    @GetMapping("/barometr")
    public String barometer(){
        return "barometr";
    }

    @GetMapping("/regulamin")
    public String regulamin(){
        return "info/regulamin";
    }

    @GetMapping("/polityka")
    public String polityka(){
        return "info/polityka";
    }

    @GetMapping("/o-nas")
    public String oNas(){
        return "info/o-aplikacji";
    }
}
