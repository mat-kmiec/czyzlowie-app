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





}
