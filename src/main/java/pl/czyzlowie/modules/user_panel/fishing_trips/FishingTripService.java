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

@Service
@RequiredArgsConstructor
public class FishingTripService {

    private final FishingTripRepository tripRepository;
    private final UserProfileService userService;

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

    public Page<FishingTrip> getUserTrips(String userEmail, Pageable pageable) {
        User user = userService.getUserByEmail(userEmail);

        return tripRepository.findAllByUserIdOrderByStartDateDesc(user.getId(), pageable);
    }

    @Transactional
    public void deleteTrip(Long tripId, String userEmail) {
        User user = userService.getUserByEmail(userEmail);

        FishingTrip trip = tripRepository.findByIdAndUserId(tripId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Wyprawa nie istnieje lub nie masz do niej uprawnień."));

        tripRepository.delete(trip);
    }

    public Integer getTotalCaughtFish(String userEmail) {
        User user = userService.getUserByEmail(userEmail);
        return tripRepository.sumCaughtFishByUserId(user.getId());
    }

    public String getFavoriteFishingMethod(String userEmail) {
        User user = userService.getUserByEmail(userEmail);
        List<FishingType> favoriteTypes = tripRepository.findFavoriteFishingTypes(user.getId());

        if (favoriteTypes != null && !favoriteTypes.isEmpty()) {
            return favoriteTypes.get(0).name();
        }
        return "BRAK";
    }
}
