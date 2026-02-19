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
        return "login";
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }

    @GetMapping("/forgot-password")
    public String forgotPass(){
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPass(){
        return "reset-password";
    }

    @GetMapping("/error")
    public String err(){
        return "error";
    }

    @GetMapping("/barometr")
    public String barometer(){
        return "barometr";
    }
}
