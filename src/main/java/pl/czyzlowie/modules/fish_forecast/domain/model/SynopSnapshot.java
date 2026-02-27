package pl.czyzlowie.modules.fish_forecast.domain.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a single snapshot of synoptic weather data at a specific point in time.
 * This object unifies data from physical meteorological stations (IMGW) and
 * numerical weather models (Virtual Stations / OpenMeteo).
 * * Wrapper classes (e.g., BigDecimal, Integer) are used instead of primitives
 * to safely handle missing data (nulls) when a specific data source does not
 * provide a particular metric (e.g., IMGW lacking cloud cover data).
 *
 * @param timestamp           The exact date and time this weather data applies to.
 * @param temperature         Air temperature in degrees Celsius.
 * @param pressure            Atmospheric pressure in hectopascals (hPa). Crucial for fish activity rules.
 * @param windSpeed           Wind speed, unified to a single unit (e.g., km/h).
 * @param windDirection       Wind direction in meteorological degrees (0-360).
 * @param humidity            Relative air humidity percentage (0-100).
 * @param precipitation       Total precipitation amount (e.g., in millimeters).
 * @param cloudCover          Cloud cover percentage (0-100). May be null for raw IMGW data.
 * @param apparentTemperature Perceived "feels-like" temperature. May be null for raw IMGW data.
 * @param uvIndex             UV index value. May be null for raw IMGW data.
 * @param windGusts           Wind gust speed, unified to a single unit (e.g., km/h) (may be null for raw IMGW data).
 */
@Builder
public record SynopSnapshot(
        LocalDateTime timestamp,
        BigDecimal temperature,
        BigDecimal pressure,
        BigDecimal windSpeed,
        Integer windDirection,
        BigDecimal humidity,
        BigDecimal precipitation,

        // --- Extended fields (can be null if source doesn't provide them) ---
        BigDecimal windGusts,           // From Virtual and Forecast
        Integer cloudCover,             // From Forecast
        BigDecimal apparentTemperature, // From Virtual and Forecast
        BigDecimal uvIndex              // From Forecast
) {}
