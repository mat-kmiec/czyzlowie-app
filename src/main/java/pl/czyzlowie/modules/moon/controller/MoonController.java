package pl.czyzlowie.modules.moon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.czyzlowie.modules.moon.entity.MoonGlobalData;
import pl.czyzlowie.modules.moon.entity.MoonStationData;
import pl.czyzlowie.modules.moon.service.MoonViewService;
import pl.czyzlowie.modules.moon.service.SolunarCalculator;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Controller
@RequestMapping("/ksiezyc")
@RequiredArgsConstructor
public class MoonController {

    private final MoonViewService moonViewService;
    private final SolunarCalculator solunarCalculator;


    @GetMapping()
    public String getMoonPage(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) String name,
            Model model) {

        LocalDate selectedDate = (date != null) ? date : LocalDate.now();
        model.addAttribute("selectedDate", selectedDate);

        int targetYear = (year != null) ? year : selectedDate.getYear();
        int targetMonth = (month != null) ? month : selectedDate.getMonthValue();
        YearMonth yearMonth = YearMonth.of(targetYear, targetMonth);

        model.addAttribute("currentMonthName", yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("pl"))));
        model.addAttribute("prevMonth", yearMonth.minusMonths(1));
        model.addAttribute("nextMonth", yearMonth.plusMonths(1));
        MoonGlobalData targetGlobal = moonViewService.getGlobalDataForDate(selectedDate);
        model.addAttribute("todayGlobal", targetGlobal);
        model.addAttribute("todayActivity", solunarCalculator.calculateActivity(targetGlobal.getPhaseEnum()));

        MoonStationData stationData = moonViewService.getStationDataForDate(lat, lon, selectedDate);
        model.addAttribute("stationData", stationData);
        MoonGlobalData nextBestDay = moonViewService.getNextExcellentFishingDay(selectedDate);
        model.addAttribute("nextBestDay", nextBestDay);
        model.addAttribute("calendarDays", moonViewService.getCalendarForMonth(yearMonth));
        model.addAttribute("locationName", (name != null && !name.isBlank()) ? name : "Warszawa");
        model.addAttribute("lat", lat);
        model.addAttribute("lon", lon);

        return "essentials/moon";
    }
}