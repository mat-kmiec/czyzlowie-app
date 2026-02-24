package pl.czyzlowie.modules.fish.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Categorizes fish species into broad groups based on their biological nature and feeding habits.
 *
 * This classification is used throughout the application to filter the fish atlas,
 * organize search results, and apply different sets of default logic within the
 * forecasting and tackle recommendation modules.
 *
 * Values:
 * - PREDATOR: Species that primarily hunt other fish or aquatic animals (e.g., Pike, Zander, Perch).
 * - PEACEFUL: Also known as "coarse fish" or "whitefish," these species primarily feed on
 * plants, insects, or small invertebrates (e.g., Carp, Bream, Roach).
 */
@Getter
@RequiredArgsConstructor
public enum FishCategory {
    PREDATOR("Drapieżniki"),
    PEACEFUL("Białoryb (Spokojnego żeru)");

    private final String displayName;
}
