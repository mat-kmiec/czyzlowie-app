package pl.czyzlowie.modules.moon.projection;

import java.math.BigDecimal;

/**
 * A projection interface for retrieving station coordinates and their associated identifiers.
 * This interface is typically used in conjunction with database queries to fetch select attributes
 * of virtual stations, specifically their ID, latitude, and longitude.
 *
 * Purpose:
 * - Simplifies the retrieval of station location data by encapsulating only the necessary fields.
 * - Optimized for scenarios where only station coordinates (latitude, longitude) and IDs are needed.
 *
 * Methods:
 * - Provides getter methods for fetching the unique identifier, latitude, and longitude of a station.
 *
 * Usage Context:
 * - Commonly used in repositories such as {@code VirtualStationRepository} for querying the database
 *   with projections.
 * - Ideal for efficient data transfer in situations where a complete {@code VirtualStation} entity
 *   is not required.
 */
public interface StationCoordinatesView {
    String getId();
    BigDecimal getLatitude();
    BigDecimal getLongitude();
}
