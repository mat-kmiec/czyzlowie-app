package pl.czyzlowie.modules.sun.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.czyzlowie.modules.sun.dto.SunScheduleDto;
import pl.czyzlowie.modules.sun.service.SunViewService;

import java.time.LocalDate;

@Controller
@RequestMapping("/wschody-zachody")
@RequiredArgsConstructor
public class SunController {

    private final SunViewService sunViewService;

    @GetMapping
    public String getSunPage(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false, name = "miasto") String miasto,
            Model model) {

        LocalDate selectedDate = (date != null) ? date : LocalDate.now();
        String locationName = (miasto != null && !miasto.isBlank()) ? miasto : "Warszawa";
        SunScheduleDto sunSchedule = sunViewService.getSunScheduleForDate(lat, lon, selectedDate);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("locationName", locationName);
        model.addAttribute("sun", sunSchedule);

        return "essentials/sun";
    }
}
