package pl.czyzlowie.modules.fish_forecast.domain.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a consolidated snapshot of astronomical data (Moon and Sun) for a single day.
 * This domain object elegantly merges global lunar data (e.g., phases, illumination)
 * with location-specific station data (e.g., sunrise, moonset).
 *
 * In the context of the fish forecast engine, this object is typically stored
 * in a daily timeline (List&lt;MoonSnapshot&gt;). Rules evaluate this data to determine
 * the impact of Solunar Theory, prime feeding times (e.g., moon transit), and
 * photoperiods (sunrise/sunset transitions).
 *
 * @param date            The specific day this astronomical data applies to. Acts as the primary timeline index.
 * @param phaseName       The English or system-level name of the moon phase (e.g., "FULL_MOON", "NEW_MOON").
 * @param illuminationPct The percentage of the Moon's surface illuminated (0.0 to 100.0).
 * @param moonAgeDays     The age of the lunar cycle in days (0.0 to ~29.53).
 * @param isSuperMoon     True if the moon is at perigee (closest to Earth), causing stronger gravitational tides.
 * * @param moonrise        The exact local time the moon appears above the horizon.
 * @param moonset         The exact local time the moon disappears below the horizon.
 * @param transit         The time the moon crosses the local meridian (highest point in the sky). Often a prime feeding trigger.
 * * @param sunrise         The exact local time the sun appears above the horizon. Critical for morning bite windows.
 * @param sunset          The exact local time the sun disappears below the horizon. Critical for evening bite windows.
 */
@Builder
public record MoonSnapshot(
        // --- Index ---
        LocalDate date,

        // --- Global Lunar Properties ---
        String phaseName,
        BigDecimal illuminationPct,
        BigDecimal moonAgeDays,
        Boolean isSuperMoon,

        // --- Localized Lunar Events ---
        LocalDateTime moonrise,
        LocalDateTime moonset,
        LocalDateTime transit,

        // --- Localized Solar Events ---
        LocalDateTime sunrise,
        LocalDateTime sunset
) {}
