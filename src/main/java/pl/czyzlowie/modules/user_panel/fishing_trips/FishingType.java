package pl.czyzlowie.modules.user_panel.fishing_trips;

/**
 * Represents the type or method of fishing associated with a fishing trip.
 *
 * Enum Constants:
 * - SPINNING: Represents fishing using spinning equipment and lures.
 * - KARPIOWANIE: Represents carp fishing, typically targeted at catching carp fish using specialized methods.
 * - FEEDER: Represents fishing using a feeder rod, often involving groundbait to attract fish.
 * - SPLAWIK: Represents fishing with a float, typically for still water or slow-moving streams.
 * - MORSKIE: Represents sea or marine fishing, typically conducted in saltwater environments.
 *
 * This enumeration is associated with the FishingTrip entity to specify the fishing technique used during a trip.
 */
public enum FishingType {
    SPINNING, KARPIOWANIE, FEEDER, SPLAWIK, MORSKIE
}

