package pl.czyzlowie.modules.user_panel.trip_calendar;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.user.entity.User;
import pl.czyzlowie.modules.user.service.UserProfileService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The TripCalendarService class provides services for managing user trips within a calendar system.
 * It handles creating, updating, retrieving, and deleting trips, as well as enforcing business rules
 * such as date validation and preventing overlapping trips.
 */
@Service
@RequiredArgsConstructor
public class TripCalendarService {

    private final TripCalendarRepository repository;
    private final TripCalendarMapper mapper;
    private final UserProfileService userService;

    /**
     * Creates a new trip based on the provided request and associates it with the user identified by the given email.
     * Validates the trip dates and ensures there are no overlapping trips for the user.
     *
     * @param request   the details of the trip to be created, including name, location, dates, etc.
     * @param userEmail the email of the user for whom the trip is being created
     * @return the created trip details encapsulated in a TripCalendarDto
     * @throws IllegalArgumentException if the trip dates are invalid or if overlapping trips exist
     */
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

    /**
     * Updates an existing trip based on the provided trip ID, request data, and user email.
     *
     * @param tripId the unique identifier of the trip to be updated
     * @param request the request object containing the updated trip details
     * @param userEmail the email of the user attempting to update the trip
     * @return a Data Transfer Object (DTO) representing the updated trip
     */
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

    /**
     * Retrieves a list of trips associated with a user, ordered by start date in descending order.
     *
     * @param userEmail The email address of the user whose trips are to be retrieved.
     * @return A list of TripCalendarDto objects representing the user's trips.
     */
    @Transactional(readOnly = true)
    public List<TripCalendarDto> getUserTrips(String userEmail) {
        User user = userService.getUserByEmail(userEmail);
        List<TripCalendar> trips = repository.findByUserIdOrderByStartDateDesc(user.getId());
        return trips.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    /**
     * Retrieves the next trip scheduled for the user identified by the given email.
     * The method finds the nearest upcoming trip (based on the current date)
     * and returns it as a data transfer object. If no upcoming trip is found, it returns null.
     *
     * @param userEmail the email of the user for whom the next trip is to be retrieved
     * @return a TripCalendarDto representing the next trip for the user,
     *         or null if no upcoming trip exists
     */
    @Transactional(readOnly = true)
    public TripCalendarDto getNextTrip(String userEmail) {
        User user = userService.getUserByEmail(userEmail);
        TripCalendar nextTrip = repository.findFirstByUserIdAndEndDateAfterOrderByStartDateAsc(user.getId(), LocalDateTime.now());
        return nextTrip != null ? mapper.toDto(nextTrip) : null;
    }

    @Transactional(readOnly = true)
    public Page<TripCalendarDto> getUpcomingTrips(String userEmail, Long excludeTripId, int page) {
        User user = userService.getUserByEmail(userEmail);
        PageRequest pageRequest = PageRequest.of(page, 5);
        return repository.findByUserIdAndEndDateAfterAndIdNotOrderByStartDateAsc(
                        user.getId(), LocalDateTime.now(), excludeTripId, pageRequest)
                .map(mapper::toDto);
    }

    /**
     * Counts the number of upcoming trips for the user identified by the given email.
     * A trip is considered upcoming if its end date is after the current date and time.
     *
     * @param userEmail the email of the user for whom the count of upcoming trips is to be retrieved
     * @return the total number of upcoming trips for the specified user
     */
    @Transactional(readOnly = true)
    public long countUpcomingTrips(String userEmail) {
        User user = userService.getUserByEmail(userEmail);
        return repository.countByUserIdAndEndDateAfter(user.getId(), LocalDateTime.now());
    }

    /**
     * Deletes a trip associated with the given trip ID and user email.
     * Ensures that the trip belongs to the user attempting to perform the deletion.
     * Throws an exception if the trip does not exist or if the user does not have permission to delete it.
     *
     * @param tripId    the unique identifier of the trip to be deleted
     * @param userEmail the email of the user attempting to delete the trip
     * @throws IllegalArgumentException if the trip does not exist or if the user does not have permission to delete the trip
     */
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
        List<TripCalendar> overlappingTrips = repository.findOverlappingTrips(userId, newStart, newEnd, excludeTripId);

        if (!overlappingTrips.isEmpty()) {
            TripCalendar trip = overlappingTrips.get(0);
            throw new IllegalArgumentException("W tym terminie (" +
                    trip.getStartDate().toLocalDate() + " - " + trip.getEndDate().toLocalDate() +
                    ") masz już zaplanowaną wyprawę: '" + trip.getName() + "'. Wybierz inny termin.");
        }
    }

    private void validateDates(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Data zakończenia nie może być wcześniejsza niż data rozpoczęcia.");
        }
    }
}