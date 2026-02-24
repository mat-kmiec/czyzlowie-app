package pl.czyzlowie.modules.fish.dto;

import lombok.Builder;
import lombok.Data;
import pl.czyzlowie.modules.fish.entity.enums.FishCategory;

/**
 * A lightweight Data Transfer Object (DTO) used for displaying fish species in list-based views.
 *
 * This DTO is designed to optimize performance by carrying only the essential information
 * required for catalog views, search results, and category listings. It avoids the
 * heavy overhead of detailed habitat or algorithm parameters included in the full
 * species details.
 *
 * Included fields:
 * - Basic identification: ID, name, and Latin name for biological reference.
 * - Navigation: The unique slug used for generating SEO-friendly URLs to the details page.
 * - Visuals: The image URL for thumbnail rendering in the UI.
 * - Classification: The fish category to support visual grouping or filtering by type.
 *
 * This object is typically mapped from the FishSpecies entity using a mapper.
 */
@Data
@Builder
public class FishListElementDto {
    private Long id;
    private String name;
    private String latinName;
    private String slug;
    private String imgUrl;
    private FishCategory category;
}
