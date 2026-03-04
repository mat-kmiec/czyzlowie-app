package pl.czyzlowie.modules.fish_forecast.domain.model;

import lombok.Builder;
import pl.czyzlowie.modules.fish.entity.ActivityCalendar;
import pl.czyzlowie.modules.fish.entity.FishAlgorithmParams;
import pl.czyzlowie.modules.fish.entity.enums.FishCategory;

/**
 * Represents a comprehensive profile of a fish species, encapsulating key biological,
 * ecological, and algorithmic details for predictive activity modeling.
 *
 * This immutable data structure serves as a central entity that connects various components
 * of the fish forecasting system and aids in understanding species-specific behavior.
 *
 * Fields:
 * - id: Unique identifier for the fish profile.
 * - name: The common name of the fish species.
 * - isGeneralBiomass: Flag indicating if the species is included in the general biomass category.
 * - category: Categorization of the fish species (e.g., predator, peaceful) using the FishCategory enum.
 * - calendar: The activity calendar detailing the species' monthly activity levels.
 * - params: Algorithmic parameters used to calculate predictive activity scores for the species.
 */
@Builder
public record FishProfile(
        Long id,
        String name,
        boolean isGeneralBiomass,
        FishCategory category,
        ActivityCalendar calendar,
        FishAlgorithmParams params
) {}