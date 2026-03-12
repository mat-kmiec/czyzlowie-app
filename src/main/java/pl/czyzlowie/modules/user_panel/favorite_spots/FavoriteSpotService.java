package pl.czyzlowie.modules.user_panel.favorite_spots;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.user.entity.User;
import pl.czyzlowie.modules.user.repository.UserRepository;

import java.util.List;

/**
 * Service class for managing favorite fishing spots of users. It provides functionality
 * related to retrieving, saving, and deleting favorite spots. Additionally, it supports
 * obtaining statistical information about a user's preferences such as favorite water type
 * and main fish target.
 *
 * This class interacts with repositories for database operations and performs business logic
 * for handling favorite spots.
 */
@Service
@RequiredArgsConstructor
public class FavoriteSpotService {

    private final FavoriteSpotRepository favoriteSpotRepository;
    private final FavoriteSpotMapper mapper;
    private final UserRepository userRepository;

    /**
     * Retrieves a paginated list of favorite fishing spots for a specific user.
     * The results are sorted in descending order of their creation date.
     *
     * @param principalName the email or username of the authenticated user whose favorite spots are to be retrieved
     * @param pageable the pagination information, including page number, size, and sorting
     * @return a paginated list of favorite spots as a Page of FavoriteSpotResponse objects
     */
    @Transactional(readOnly = true)
    public Page<FavoriteSpotResponse> getUserSpots(String principalName, Pageable pageable) {
        return favoriteSpotRepository.findAllByUserEmailOrderByCreatedAtDesc(principalName, pageable)
                .map(mapper::toDto);
    }

    /**
     * Retrieves the total number of favorite spots associated with the specified user.
     *
     * @param principalName the email or username of the user whose favorite spot count is to be retrieved
     * @return the total count of favorite spots for the specified user
     */
    @Transactional(readOnly = true)
    public long getTotalSpots(String principalName) {
        return favoriteSpotRepository.countByUserEmail(principalName);
    }

    /**
     * Retrieves the favorite water type of a user based on their activity.
     *
     * @param principalName the name or email of the principal (user) whose favorite water type is to be determined
     * @return a string representing the favorite water type of the user; if no data is available, returns "Brak danych"
     */
    @Transactional(readOnly = true)
    public String getFavoriteWaterType(String principalName) {
        List<WaterType> types = favoriteSpotRepository.findMostFrequentWaterTypeByUserEmail(principalName, PageRequest.of(0, 1));
        if (types.isEmpty()) return "Brak danych";

        return switch (types.get(0)) {
            case RIVER -> "Rzeki";
            case LAKE -> "Jeziora";
            case POND -> "Stawy / PZW";
            case COMMERCIAL -> "Komercje";
            case SEA -> "Morze / Zatoki";
        };
    }

    /**
     * Retrieves the main target (most frequent fish tag) associated with the given principal's email.
     * The method performs a read-only transaction to fetch data from the database.
     *
     * @param principalName the email address of the user whose most frequent fish tag is to be retrieved
     * @return the most frequent fish tag for the user; returns "Brak danych" if no data is available
     */
    @Transactional(readOnly = true)
    public String getMainTarget(String principalName) {
        List<String> tags = favoriteSpotRepository.findMostFrequentFishTagByUserEmail(principalName, PageRequest.of(0, 1));
        return tags.isEmpty() ? "Brak danych" : tags.get(0);
    }

    /**
     * Saves a favorite fishing spot for a user.
     *
     * @param request       the data for the favorite spot to be saved, including name, location, and details
     * @param principalName the email or identifier of the authenticated user saving the spot
     */
    @Transactional
    public void saveSpot(FavoriteSpotRequest request, String principalName) {
        User user = userRepository.findByEmail(principalName)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika"));

        FavoriteSpot spot = FavoriteSpot.builder()
                .user(user)
                .name(request.getSpotName())
                .locationDisplay(request.getLocation())
                .latitude(request.getLat())
                .longitude(request.getLng())
                .waterType(WaterType.valueOf(request.getWaterType().toUpperCase()))
                .fishTags(request.getFishTags() != null ? request.getFishTags() : List.of())
                .note(request.getNote())
                .build();

        favoriteSpotRepository.save(spot);
    }

    /**
     * Deletes a specific spot associated with a user.
     *
     * @param spotId the unique identifier of the spot to be deleted
     * @param principalName the email or username of the user requesting the deletion
     * @throws IllegalArgumentException if the spot is not found or if the user does not have permission to delete it
     */
    @Transactional
    public void deleteSpot(Long spotId, String principalName) {
        FavoriteSpot spot = favoriteSpotRepository.findByIdAndUserEmail(spotId, principalName)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono miejscówki lub brak uprawnień"));

        favoriteSpotRepository.delete(spot);
    }
}