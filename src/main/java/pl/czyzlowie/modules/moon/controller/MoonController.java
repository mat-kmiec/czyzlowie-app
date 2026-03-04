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

/**
 * Controller to manage requests related to the Moon's data visualization and related calculations.
 * This includes fetching global moon data, activity levels, and other relevant information for a particular date,
 * as well as generating a monthly calendar for lunar events.
 */
@Controller
@RequestMapping("/ksiezyc")
@RequiredArgsConstructor
public class MoonController {

    private final MoonViewService moonViewService;
    private final SolunarCalculator solunarCalculator;


    /**
     * Handles HTTP GET requests to retrieve the moon page with detailed lunar and fishing-related information.
     * Processes optional query parameters to customize the displayed data such as date, location, and calendar month.
     *
     * @param date Optional. A specific date to retrieve moon data for. If not provided, the current date is used.
     * @param year Optional. The year for generating the calendar view. If not provided, the year from the selected date is used.
     * @param month Optional. The month for generating the calendar view. If not provided, the month from the selected date is used.
     * @param lat Optional. The latitude of the location to retrieve moon and station data for. If not provided, defaults are applied.
     * @param lon Optional. The longitude of the location to retrieve moon and station data for. If not provided, defaults are applied.
     * @param name Optional. The name of the location to display. If not provided, defaults to "Warszawa".
     * @param model The model to populate with attributes required for rendering the moon page.
     * @return The name of the view template for the moon page, "essentials/moon".
     */
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