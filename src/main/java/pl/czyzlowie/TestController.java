package pl.czyzlowie;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {


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
        return "error/error";
    }



    @GetMapping("/kalendarz-wypraw")
    public String kalendarzWypraw(){
        return "profil/trip-calendar";
    }

    @GetMapping("/ulubione-miejscowki")
    public String ulubioneMiejsca(){
        return "profil/favorite-places";
    }

    @GetMapping("/lista-przynet")
    public String listaPrzynet(){
        return "profil/lure-list";
    }

    @GetMapping("/moje-zestawy")
    public String mojeZestawy(){
        return "profil/my-sets";
    }

    @GetMapping("checklisty")
    public String checklisty(){
        return "profil/equipment-checklist";
    }

    @GetMapping("/notatki")
    public String wyprawy(){
        return "profil/private-notes";
    }

    @GetMapping("/cele-wedkarskie")
    public String celeWedkarskie(){
        return "profil/fishing-goals";
    }



}
