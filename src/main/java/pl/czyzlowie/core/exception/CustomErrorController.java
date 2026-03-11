package pl.czyzlowie.core.exception;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            model.addAttribute("status", statusCode);

            if (statusCode == 404) {
                model.addAttribute("error", "Nie znaleziono strony");
            } else if (statusCode == 403) {
                model.addAttribute("error", "Odmowa dostępu");
            } else if (statusCode == 500) {
                model.addAttribute("error", "Wewnętrzny błąd serwera");
            } else if (statusCode == 400) {
                model.addAttribute("error", "Nieprawidłowe żądanie");
            } else {
                model.addAttribute("error", "Nieoczekiwany problem");
            }
        } else {
            model.addAttribute("status", "Błąd");
            model.addAttribute("error", "Nieznany błąd");
        }

        return "error";
    }
}