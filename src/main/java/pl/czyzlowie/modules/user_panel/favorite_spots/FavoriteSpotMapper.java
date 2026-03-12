package pl.czyzlowie.modules.user_panel.favorite_spots;

import org.springframework.stereotype.Component;

/**
 * Mapper class responsible for converting FavoriteSpot entities into FavoriteSpotResponse DTOs.
 * Provides a method for mapping the entity data to a corresponding response object.
 */
@Component
public class FavoriteSpotMapper {

    public FavoriteSpotResponse toDto(FavoriteSpot spot) {
        return FavoriteSpotResponse.builder()
                .id(spot.getId())
                .name(spot.getName())
                .locationDisplay(spot.getLocationDisplay())
                .lat(spot.getLatitude())
                .lng(spot.getLongitude())
                .waterType(spot.getWaterType().name().toLowerCase())
                .fishTags(spot.getFishTags())
                .note(spot.getNote())
                .build();
    }
}