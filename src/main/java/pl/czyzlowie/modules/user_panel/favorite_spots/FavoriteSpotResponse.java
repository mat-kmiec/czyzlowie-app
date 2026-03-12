package pl.czyzlowie.modules.user_panel.favorite_spots;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * Represents a response object for a favorite spot. This class contains
 * details about a specific favorite fishing or recreational spot, including
 * its location, characteristics, and associated metadata.
 *
 * This class is annotated with @Data and @Builder to automatically generate
 * boilerplate code and provide a fluent interface for creating instances.
 *
 * Fields:
 * - id: Unique identifier for the favorite spot.
 * - name: Name of the favorite spot.
 * - locationDisplay: Human-readable description of the spot's location.
 * - lat: Latitude coordinate of the spot.
 * - lng: Longitude coordinate of the spot.
 * - waterType: Describes the type of water at the spot (e.g., freshwater, saltwater).
 * - fishTags: List of fish types commonly found at the spot.
 * - photoUrl: URL pointing to an image of the spot.
 * - note: Freeform note or description related to the spot.
 */
@Data
@Builder
public class FavoriteSpotResponse {
    private Long id;
    private String name;
    private String locationDisplay;
    private BigDecimal lat;
    private BigDecimal lng;
    private String waterType;
    private List<String> fishTags;
    private String photoUrl;
    private String note;
}