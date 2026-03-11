package pl.czyzlowie.modules.user_panel.fishing_trips;

/**
 * Represents tags or attributes that can be associated with a fishing trip.
 * These tags describe the characteristics or specific nature of the trip.
 *
 * Enum Constants:
 * - BOAT: Indicates that the fishing trip involves a boat.
 * - NIGHT: Indicates that the fishing trip takes place at night.
 * - ICE: Indicates that the fishing trip involves ice fishing.
 * - PONTOON: Indicates that the fishing trip involves a pontoon.
 * - WADING: Indicates that the fishing trip involves wading.
 * - COMPETITION: Indicates that the fishing trip is part of a fishing competition.
 *
 * This enumeration is used to classify or tag fishing trips based on their distinctive features. It is typically associated with
 * the FishingTrip entity to provide additional descriptive data about the trip.
 */
public enum TripTag {
    BOAT, NIGHT, ICE, PONTOON, WADING, COMPETITION
}
