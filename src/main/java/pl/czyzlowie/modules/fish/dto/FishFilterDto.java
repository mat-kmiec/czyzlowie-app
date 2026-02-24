package pl.czyzlowie.modules.fish.dto;

import lombok.Data;
import pl.czyzlowie.modules.fish.entity.enums.FishCategory;

/**
 * A Data Transfer Object (DTO) used to capture search and filter criteria from the user interface.
 *
 * This class facilitates the filtering of the fish atlas by binding request parameters
 * from search forms. It is typically used in conjunction with JPA Specifications to
 * dynamically build database queries based on the provided criteria.
 *
 * Filter criteria:
 * - name: A string used for partial, case-insensitive matching against the species' name.
 * - category: A specific FishCategory (e.g., Predator or Peaceful) to narrow down the results.
 *
 * If a field is null or empty, the corresponding filter is ignored during the query execution.
 */
@Data
public class FishFilterDto {
    private String name;
    private FishCategory category;
}
