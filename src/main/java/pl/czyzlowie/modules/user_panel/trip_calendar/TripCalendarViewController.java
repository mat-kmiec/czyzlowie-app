package pl.czyzlowie.modules.user_panel.trip_calendar;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller responsible for managing the Trip Calendar view.
 * It provides endpoints for displaying, creating, editing, and deleting trips.
 */
@Controller
@RequestMapping("/kalendarz-wypraw")
@RequiredArgsConstructor
public class TripCalendarViewController {

    private final TripCalendarService service;
    private final ObjectMapper objectMapper;

    /**
     * Displays the trip calendar for the authenticated user. Retrieves and processes
     * details about the user's next trip, upcoming trips, and all trips, and provides
     * the data to the model for rendering in the corresponding view.
     *
     * @param principal the authentication principal representing the currently logged-in user
     * @param model the model used to bind attributes for rendering in the view
     * @param page the current page number for paginated upcoming trips; default is 0
     * @return the name of the view to be rendered for the trip calendar page
     */
    @GetMapping({"", "/"})
    public String showTripCalendar(Principal principal, Model model,
                                   @RequestParam(defaultValue = "0") int page) {
        String email = principal.getName();
        LocalDateTime now = LocalDateTime.now();

        TripCalendarDto nextTrip = service.getNextTrip(email);
        model.addAttribute("nextTrip", nextTrip);

        if (nextTrip != null) {
            if (nextTrip.getStartDate().isBefore(now) && nextTrip.getEndDate().isAfter(now)) {
                model.addAttribute("nextTripCountdown", "Trwa obecnie");
            } else {
                long days = Duration.between(now, nextTrip.getStartDate()).toDays();
                model.addAttribute("nextTripCountdown", days == 0 ? "Dzisiaj!" : "Za " + days + " dni");
            }
        }

        Long excludeId = nextTrip != null ? nextTrip.getId() : -1L;
        Page<TripCalendarDto> upcomingTripsPage = service.getUpcomingTrips(email, excludeId, page);

        model.addAttribute("upcomingTrips", upcomingTripsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", Math.max(1, upcomingTripsPage.getTotalPages()));
        model.addAttribute("hasNextPage", upcomingTripsPage.hasNext());
        model.addAttribute("hasPreviousPage", upcomingTripsPage.hasPrevious());
        model.addAttribute("totalTrips", service.countUpcomingTrips(email));
        model.addAttribute("createRequest", new CreateTripCalendarRequest());

        try {
            List<TripCalendarDto> allTrips = service.getUserTrips(email);
            List<Map<String, Object>> missionsList = allTrips.stream()
                    .map(this::mapTripToJson)
                    .collect(Collectors.toList());
            model.addAttribute("missionsJson", objectMapper.writeValueAsString(missionsList));
        } catch (Exception e) {
            model.addAttribute("missionsJson", "[]");
        }

        return "profil/trip-calendar";
    }

    /**
     * Handles the creation of a trip event for the authenticated user. Validates the trip creation request,
     * interacts with the service layer to persist the trip data, and provides appropriate success or error
     * feedback messages through redirect attributes.
     *
     * @param request the input data for trip creation, encapsulated in a CreateTripCalendarRequest object
     * @param bindingResult contains validation result for the provided CreateTripCalendarRequest data
     * @param principal the authentication principal representing the currently logged-in user
     * @param redirectAttributes used for adding attributes to be exposed during a redirect scenario
     * @return a redirect URL back to the trip calendar view
     */
    @PostMapping({"", "/"})
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

    /**
     * Handles the editing of an existing trip for the authenticated user.
     * Validates the provided trip details and updates the corresponding trip.
     * Sets an appropriate success or error message as a flash attribute.
     *
     * @param id the unique identifier of the trip to be edited
     * @param request the request object containing the updated trip details
     * @param bindingResult the result of request validation, containing validation errors if present
     * @param principal the authentication principal representing the currently logged-in user
     * @param redirectAttributes attributes used to provide feedback messages to the redirected view
     * @return a redirect to the trip calendar page after attempting to update the trip
     */
    @PostMapping({"/{id}/edytuj", "/{id}/edytuj/"})
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

    /**
     * Deletes a trip with the specified ID associated with the current user's principal.
     * Attempts to delete the trip and provides feedback messages to the user.
     *
     * @param id the ID of the trip to be deleted
     * @param principal the security principal representing the authenticated user
     * @param redirectAttributes attributes for flash messages to provide feedback on success or failure
     * @return a redirect path to the trip calendar page
     */
    @PostMapping({"/{id}/usun", "/{id}/usun/"})
    public String deleteTrip(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            service.deleteTrip(id, principal.getName());
            redirectAttributes.addFlashAttribute("success", "Wyprawa została usunięta.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/kalendarz-wypraw";
    }

    /**
     * Converts a TripCalendarDto object into a map representation suitable for JSON serialization.
     *
     * @param t the TripCalendarDto object containing trip details to be mapped
     * @return a Map containing key-value pairs of trip details including id, start and end dates,
     *         location, coordinates, method, team, and notes
     */
    private Map<String, Object> mapTripToJson(TripCalendarDto t) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", t.getId());
        map.put("startIso", t.getStartDate().toString());
        map.put("endIso", t.getEndDate().toString());
        map.put("title", t.getName());
        map.put("location", t.getLocation());
        map.put("latitude", t.getLatitude());
        map.put("longitude", t.getLongitude());
        map.put("start", t.getStartDate().toLocalTime().toString());
        map.put("end", t.getEndDate().toLocalTime().toString());
        map.put("method", t.getMethod() != null ? t.getMethod().toString() : "");
        map.put("team", t.getTeam());
        map.put("notes", t.getNotes());
        return map;
    }
}