package pl.czyzlowie.modules.user_panel.trip_calendar;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.user.entity.User;
import pl.czyzlowie.modules.user.service.UserProfileService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripCalendarService {

    private final TripCalendarRepository repository;
    private final TripCalendarMapper mapper;
    private final UserProfileService userService;

    @Transactional
    public TripCalendarDto createTrip(CreateTripCalendarRequest request, String userEmail) {
        validateDates(request.getStartDate(), request.getEndDate());

        User user = userService.getUserByEmail(userEmail);

        checkForOverlappingTrips(request.getStartDate(), request.getEndDate(), user.getId(), null);

        TripCalendar trip = mapper.toEntity(request);
        trip.setUser(user);

        TripCalendar saved = repository.save(trip);
        return mapper.toDto(saved);
    }

    @Transactional
    public TripCalendarDto updateTrip(Long tripId, CreateTripCalendarRequest request, String userEmail) {
        validateDates(request.getStartDate(), request.getEndDate());

        User user = userService.getUserByEmail(userEmail);
        TripCalendar trip = repository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono wyprawy."));

        if (!trip.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Brak uprawnień do edycji tej wyprawy.");
        }

        checkForOverlappingTrips(request.getStartDate(), request.getEndDate(), user.getId(), tripId);

        trip.setName(request.getName());
        trip.setLocation(request.getLocation());
        trip.setLatitude(request.getLatitude());
        trip.setLongitude(request.getLongitude());
        trip.setStartDate(request.getStartDate());
        trip.setEndDate(request.getEndDate());
        trip.setMethod(request.getMethod());
        trip.setTeam(request.getTeam());
        trip.setNotes(request.getNotes());

        TripCalendar saved = repository.save(trip);
        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<TripCalendarDto> getUserTrips(String userEmail) {
        User user = userService.getUserByEmail(userEmail);
        List<TripCalendar> trips = repository.findByUserIdOrderByStartDateDesc(user.getId());
        return trips.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TripCalendarDto> getTripsInRange(String userEmail, LocalDateTime start, LocalDateTime end) {
        User user = userService.getUserByEmail(userEmail);
        List<TripCalendar> trips = repository.findTripsInDateRange(user.getId(), start, end);
        return trips.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteTrip(Long tripId, String userEmail) {
        User user = userService.getUserByEmail(userEmail);
        TripCalendar trip = repository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono wyprawy."));
        if (!trip.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Brak uprawnień.");
        }
        repository.delete(trip);
    }

    private void checkForOverlappingTrips(LocalDateTime newStart, LocalDateTime newEnd, Long userId, Long excludeTripId) {
        List<TripCalendar> existingTrips = repository.findByUserIdOrderByStartDateDesc(userId);

        for (TripCalendar trip : existingTrips) {
            if (excludeTripId != null && trip.getId().equals(excludeTripId)) {
                continue;
            }

            if (!newStart.isAfter(trip.getEndDate()) && !newEnd.isBefore(trip.getStartDate())) {
                throw new IllegalArgumentException("W tym terminie (" +
                        trip.getStartDate().toLocalDate() + " - " + trip.getEndDate().toLocalDate() +
                        ") masz już zaplanowaną wyprawę: '" + trip.getName() + "'. Wybierz inny termin.");
            }
        }
    }

    private void validateDates(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Data zakończenia nie może być wcześniejsza niż data rozpoczęcia.");
        }
    }
}