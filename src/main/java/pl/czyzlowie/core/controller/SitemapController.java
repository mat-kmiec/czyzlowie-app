package pl.czyzlowie.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sitemap")
@RequiredArgsConstructor
public class SitemapController {

    @GetMapping
    public String showSitemap(Model model) {
        model.addAttribute("pageTitle", "Mapa Strony");
        model.addAttribute("pageDescription", "Mapa strony CzyZlowie - wszystkie ważne sekcje aplikacji do prognozy brań i kalendarza brań.");
        
        return "info/sitemap";
    }
}

