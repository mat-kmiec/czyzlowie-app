package pl.czyzlowie.modules.fish_forecast.domain.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a single snapshot of hydrological data at a specific point in time.
 * This object unifies data from physical hydrological stations (IMGW), providing
 * crucial insights into water conditions that directly affect fish metabolism,
 * positioning, and feeding behavior.
 *
 * Wrapper classes (e.g., BigDecimal, Integer) are used to safely handle missing
 * data, as not all stations are equipped with a full suite of sensors (e.g., lakes
 * typically do not report discharge, and some stations lack temperature sensors).
 *
 * @param timestamp            The unified logical time of this snapshot (e.g., aligned to 30-minute intervals).
 * @param waterLevel           Water level in centimeters (cm). Rapid changes (rising/falling) heavily impact fish activity.
 * @param waterTemperature     Water temperature in degrees Celsius. The primary driver of fish metabolism and spawning.
 * @param discharge            Water discharge/flow rate in cubic meters per second (m³/s). Vital for river currents.
 * @param icePhenomenon        Code representing ice conditions (e.g., floating ice, solid ice cover).
 * @param overgrowthPhenomenon Code representing aquatic vegetation overgrowth (weeds/algae blooms).
 */
@Builder
public record HydroSnapshot(
        LocalDateTime timestamp,
        Integer waterLevel,
        BigDecimal waterTemperature,
        BigDecimal discharge,
        Integer icePhenomenon,
        Integer overgrowthPhenomenon
) {}
