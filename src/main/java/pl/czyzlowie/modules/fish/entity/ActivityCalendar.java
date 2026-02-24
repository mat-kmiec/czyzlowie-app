package pl.czyzlowie.modules.fish.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import pl.czyzlowie.modules.fish.entity.enums.ActivityLevel;

/**
 * Defines the monthly biological activity cycle for a specific fish species throughout the calendar year.
 *
 * This class provides a baseline for seasonal behavior, mapping each month to a specific
 * activity level. It accounts for recurring biological events such as spawning periods,
 * winter dormancy (hibernation), and peak feeding seasons.
 *
 * The calendar consists of twelve monthly fields, each utilizing the ActivityLevel enum
 * to determine the species' typical intensity of movement and feeding. These values
 * are often used by the forecasting engine to provide a seasonal context to daily
 * weather-based predictions.
 *
 * It is intended to be used as an embedded component within the FishSpecies entity.
 */
@Embeddable
@Data
public class ActivityCalendar {
    @Enumerated(EnumType.STRING) private ActivityLevel janActivity;
    @Enumerated(EnumType.STRING) private ActivityLevel febActivity;
    @Enumerated(EnumType.STRING) private ActivityLevel marActivity;
    @Enumerated(EnumType.STRING) private ActivityLevel aprActivity;
    @Enumerated(EnumType.STRING) private ActivityLevel mayActivity;
    @Enumerated(EnumType.STRING) private ActivityLevel junActivity;
    @Enumerated(EnumType.STRING) private ActivityLevel julActivity;
    @Enumerated(EnumType.STRING) private ActivityLevel augActivity;
    @Enumerated(EnumType.STRING) private ActivityLevel sepActivity;
    @Enumerated(EnumType.STRING) private ActivityLevel octActivity;
    @Enumerated(EnumType.STRING) private ActivityLevel novActivity;
    @Enumerated(EnumType.STRING) private ActivityLevel decActivity;
}