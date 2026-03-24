package pl.czyzlowie.modules.user_panel.trip_calendar;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/kalendarz-wypraw")
@RequiredArgsConstructor
public class TripCalendarViewController {

    private final TripCalendarService service;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public String showTripCalendar(Principal principal, Model model,
                                   @RequestParam(defaultValue = "0") int page) {
        List<TripCalendarDto> allTrips = service.getUserTrips(principal.getName());
        LocalDateTime now = LocalDateTime.now();

        List<TripCalendarDto> upcomingTrips = allTrips.stream()
                .filter(t -> t.getEndDate().isAfter(now))
                .sorted(Comparator.comparing(TripCalendarDto::getStartDate))
                .collect(Collectors.toList());

        model.addAttribute("totalTrips", upcomingTrips.size());

        TripCalendarDto nextTrip = upcomingTrips.isEmpty() ? null : upcomingTrips.get(0);
        model.addAttribute("nextTrip", nextTrip);

        if (nextTrip != null) {
            if (nextTrip.getStartDate().isBefore(now) && nextTrip.getEndDate().isAfter(now)) {
                model.addAttribute("nextTripCountdown", "Trwa obecnie");
            } else {
                long days = Duration.between(now, nextTrip.getStartDate()).toDays();
                model.addAttribute("nextTripCountdown", days == 0 ? "Dzisiaj!" : "Za " + days + " dni");
            }
        }

        List<TripCalendarDto> paginatedUpcomingList = upcomingTrips.size() > 1
                ? upcomingTrips.subList(1, upcomingTrips.size())
                : List.of();

        int pageSize = 5;
        int totalPages = (int) Math.ceil((double) paginatedUpcomingList.size() / pageSize);
        if (totalPages == 0) totalPages = 1;
        int fromIndex = Math.min(page * pageSize, paginatedUpcomingList.size());
        int toIndex = Math.min(fromIndex + pageSize, paginatedUpcomingList.size());
        List<TripCalendarDto> paginatedTrips = paginatedUpcomingList.subList(fromIndex, toIndex);

        model.addAttribute("upcomingTrips", paginatedTrips);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("hasNextPage", page < totalPages - 1);
        model.addAttribute("hasPreviousPage", page > 0);

        model.addAttribute("createRequest", new CreateTripCalendarRequest());

        try {
            List<Map<String, Object>> missionsList = allTrips.stream().map(t -> {
                Map<String, Object> map = new java.util.HashMap<>();
                map.put("id", t.getId());
                map.put("startIso", t.getStartDate().toString());
                map.put("endIso", t.getEndDate().toString());
                map.put("title", t.getName());
                map.put("location", t.getLocation());
                map.put("latitude", t.getLatitude());
                map.put("longitude", t.getLongitude());
                map.put("start", t.getStartDate().toLocalTime().toString());
                map.put("end", t.getEndDate().toLocalTime().toString());
                map.put("method", t.getMethod().toString());
                map.put("team", t.getTeam());
                map.put("notes", t.getNotes());
                return map;
            }).collect(Collectors.toList());
            model.addAttribute("missionsJson", objectMapper.writeValueAsString(missionsList));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            model.addAttribute("missionsJson", "[]");
        }

        return "profil/trip-calendar";
    }

    @PostMapping
    public String createTrip(@Valid @ModelAttribute("createRequest") CreateTripCalendarRequest request,
                             BindingResult bindingResult, Principal principal, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/kalendarz-wypraw";
        }
        try {
            service.createTrip(request, principal.getName());
            redirectAttributes.addFlashAttribute("success", "Wyprawa została zaplanowana!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/kalendarz-wypraw";
    }

    @PostMapping("/{id}/edytuj")
    public String editTrip(@PathVariable Long id, @Valid @ModelAttribute("createRequest") CreateTripCalendarRequest request,
                           BindingResult bindingResult, Principal principal, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/kalendarz-wypraw";
        }
        try {
            service.updateTrip(id, request, principal.getName());
            redirectAttributes.addFlashAttribute("success", "Wyprawa została zaktualizowana!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/kalendarz-wypraw";
    }

    @PostMapping("/{id}/usun")
    public String deleteTrip(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            service.deleteTrip(id, principal.getName());
            redirectAttributes.addFlashAttribute("success", "Wyprawa została usunięta.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/kalendarz-wypraw";
    }
}