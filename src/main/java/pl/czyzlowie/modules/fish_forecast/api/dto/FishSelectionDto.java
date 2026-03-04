package pl.czyzlowie.modules.fish_forecast.api.dto;

import pl.czyzlowie.modules.fish.entity.enums.FishCategory;

/**
 * A Data Transfer Object (DTO) representing a selected fish entity with its basic attributes.
 *
 * This class is primarily used to pass fish-related data between application layers in a
 * concise and immutable manner, simplifying the data handling for operations such
 * as user selections or catalog filtering.
 *
 * Fields:
 *
 * - `id`: A unique identifier for the fish entity.
 * - `name`: The name of the fish species, typically in a human-readable format.
 * - `category`: The biological grouping of the fish, represented as a {@link FishCategory}.
 */
public record FishSelectionDto(
        Long id,
        String name,
        FishCategory category
) {}
