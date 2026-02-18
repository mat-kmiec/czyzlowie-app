package pl.czyzlowie.modules.moon.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents data associated with a specific moon station on a given date.
 * This entity contains information about lunar and solar events such as moonrise, moonset,
 * sunrise, sunset, and other related metrics for a particular station's location.
 * Provides the ability to store and retrieve calculations related to these celestial events
 * for tracking and analysis purposes.
 */
@Entity
@Table(name = "moon_station_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoonStationData {

    /**
     * Composite primary key for the MoonStationData entity.
     * Encapsulates the unique identification of a moon station data record,
     * consisting of a station ID, station type, and calculation date.
     */
    @EmbeddedId
    private MoonStationDataId id;

    /**
     * Represents the moonrise time for a specific station and date.
     * This value indicates the exact LocalDateTime when the moon becomes visible
     * above the horizon, relative to the location of the moon station.
     */
    private LocalDateTime moonrise;

    /**
     * Represents the moonset time for a specific station and date.
     * This value indicates the exact LocalDateTime when the moon is no longer
     * visible below the horizon, relative to the location of the moon station.
     */
    private LocalDateTime moonset;

    /**
     * Represents the exact LocalDateTime when the moon crosses the local meridian
     * at the specified station and date. The meridian crossing refers to the time
     * at which the moon is at its highest point in the sky, as seen from the station's location.
     */
    private LocalDateTime transit;

    /**
     * Represents the sunrise time for a specific station and date.
     * This value indicates the exact LocalDateTime when the sun becomes visible
     * above the horizon, relative to the location of the moon station.
     */
    private LocalDateTime sunrise;

    /**
     * Represents the sunset time for a specific station and date.
     * This value indicates the exact LocalDateTime when the sun is no longer
     * visible below the horizon, relative to the location of the moon station.
     */
    private LocalDateTime sunset;

    /**
     * Represents the total length of the day in seconds for a specific moon station and date.
     * This value is computed as the difference in seconds between sunrise and sunset times
     * and indicates the duration of daylight at the station's location for the given date.
     *
     * Mapped to the "day_length_sec" column in the database.
     */
    @Column(name = "day_length_sec")
    private Long dayLengthSec;

    /**
     * Represents the maximum altitude (in degrees) of the moon above the horizon
     * as seen from the specific moon station on a given date. This value is recorded
     * with a precision of up to 4 digits and a scale of 2 decimal places.
     * It provides a measure of how high the moon rises in the sky at its peak
     * during its transit for the location and date specified.
     *
     * Mapped to the "max_altitude" column in the database.
     */
    @Column(name = "max_altitude", precision = 4, scale = 2)
    private BigDecimal maxAltitude;
}
