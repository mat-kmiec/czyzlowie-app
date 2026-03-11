package pl.czyzlowie.modules.user_panel.fishing_trips;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.user.entity.User;
import pl.czyzlowie.modules.user.service.UserProfileService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing fishing trip-related operations
 * for users in the application. It interacts with the underlying repositories
 * to perform CRUD operations on fishing trips and provides additional user
 * statistics such as total caught fish and favorite fishing methods.
 */
@Service
@RequiredArgsConstructor
public class FishingTripService {

    private final FishingTripRepository tripRepository;
    private final UserProfileService userService;

    /**
     * Creates a new fishing trip and saves it to the repository.
     *
     * This method validates the provided trip request data, including date validity,
     * fishing type, trip rating, and tags. It throws an exception if any of the input
     * parameters are invalid.
     *
     * @param request the object containing the details of the fishing trip to be created
     * @param userEmail the email of the user creating the fishing trip
     * @throws IllegalArgumentException if the end date is earlier than the start date,
     *                                   or if invalid fishing type, rating, or tags are provided
     */
    @Transactional
    public void createTrip(CreateFishingTripRequest request, String userEmail) {
        if (!request.isDateValid()) {
            throw new IllegalArgumentException("Data zakończenia nie może być wcześniejsza niż data rozpoczęcia.");
        }

        User user = userService.getUserByEmail(userEmail);

        FishingType type;
        TripRating rating;
        try {
            type = FishingType.valueOf(request.type().toUpperCase());
            rating = TripRating.valueOf(request.rating().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Przesłano nieprawidłowe parametry wyboru.");
        }

        Set<TripTag> mappedTags = null;
        if (request.tags() != null) {
            try {
                mappedTags = request.tags().stream()
                        .map(tag -> TripTag.valueOf(tag.toUpperCase()))
                        .collect(Collectors.toSet());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Przesłano nieprawidłowe opcje/tagi.");
            }
        }

        FishingTrip trip = FishingTrip.builder()
                .user(user)
                .name(request.tripName())
                .location(request.location())
                .latitude(request.lat())
                .longitude(request.lng())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .fishingType(type)
                .caughtFish(request.caughtFish())
                .rating(rating)
                .tags(mappedTags)
                .note(request.note())
                .build();

        tripRepository.save(trip);
    }

    /**
     * Retrieves a paginated list of fishing trips associated with a specific user,
     * ordered by start date in descending order.
     *
     * @param userEmail the email address of the user whose fishing trips are to be retrieved
     * @param pageable  the pagination and sorting information
     * @return a pageable list of fishing trips for the specified user
     */
    public Page<FishingTrip> getUserTrips(String userEmail, Pageable pageable) {
        User user = userService.getUserByEmail(userEmail);

        return tripRepository.findAllByUserIdOrderByStartDateDesc(user.getId(), pageable);
    }

    /**
     * Deletes a fishing trip associated with the provided trip ID and user email.
     *
     * @param tripId the unique identifier of the trip to be deleted
     * @param userEmail the email of the user requesting the deletion of the trip
     * @throws IllegalArgumentException if the trip does not exist or the user does not have permission to delete it
     */
    @Transactional
    public void deleteTrip(Long tripId, String userEmail) {
        User user = userService.getUserByEmail(userEmail);

        FishingTrip trip = tripRepository.findByIdAndUserId(tripId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Wyprawa nie istnieje lub nie masz do niej uprawnień."));

        tripRepository.delete(trip);
    }

    /**
     * Retrieves the total number of fish caught by a user.
     *
     * @param userEmail the email address of the user whose total caught fish count is to be retrieved
     * @return the total number of fish caught by the specified user, or null if no data is available
     */
    public Integer getTotalCaughtFish(String userEmail) {
        User user = userService.getUserByEmail(userEmail);
        return tripRepository.sumCaughtFishByUserId(user.getId());
    }

    /**
     * Retrieves the favorite fishing method for a user based on their email.
     *
     * @param userEmail the email address of the user for whom the favorite fishing method is to be retrieved
     * @return the name of the favorite fishing method as a string, or "BRAK" if no favorite method is found
     */
    public String getFavoriteFishingMethod(String userEmail) {
        User user = userService.getUserByEmail(userEmail);
        List<FishingType> favoriteTypes = tripRepository.findFavoriteFishingTypes(user.getId());

        if (favoriteTypes != null && !favoriteTypes.isEmpty()) {
            return favoriteTypes.get(0).name();
        }
        return "BRAK";
    }
}
