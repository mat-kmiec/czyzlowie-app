package pl.czyzlowie.modules.moon.entity;

import jakarta.persistence.*;
import lombok.*;
import pl.czyzlowie.modules.moon.entity.Enums.MoonPhaseType;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents the global data related to the Moon for a specific date.
 * This entity captures various properties of the Moon such as its phase, illumination percentage,
 * age, distance from Earth, and whether it is a supermoon.
 *
 * The class is mapped to the "moon_global_data" table in the database.
 * It uses Lombok for automatic generation of getters, setters, builders, and constructors.
 *
 * Fields:
 * - calculationDate: The date for which the calculations are performed. This serves as the primary key.
 * - phaseEnum: Enum representing the phase of the Moon (e.g., NEW_MOON, FULL_MOON).
 * - phaseMoonPl: The name of the Moon phase in Polish.
 * - illuminationPct: The percentage of the Moon's surface that is illuminated.
 * - moonAgeDays: The age of the Moon in days, from 0 to approximately 29.53 days.
 * - isSuperMoon: Boolean indicating if the Moon qualifies as a supermoon for the given date.
 * - distanceKm: The distance of the Moon from Earth in kilometers.
 *
 * Annotations:
 * - @Entity: Marks this class as a JPA entity.
 * - @Table: Specifies the database table name.
 * - @Id: Indicates the primary key field.
 * - @Column: Used to configure details of individual table columns.
 * - @Enumerated: Specifies that the phaseEnum field should be stored as a string representation of the Enum.
 * - Lombok annotations (@Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor, @Builder) are used for
 *   automatic generation of boilerplate code such as constructors and accessor methods.
 */
@Entity
@Table(name = "moon_global_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoonGlobalData {

    /**
     * Represents the date for which moon-related calculations or data are recorded.
     *
     * This field is the primary key in the "moon_global_data" table. It indicates the specific day
     * associated with all other properties and data related to the Moon, such as its phase,
     * illumination percentage, age, distance, and whether it qualifies as a supermoon.
     *
     * Constraints:
     * - This field cannot be null as it serves as the primary identifier of the entity.
     * - It is stored in the "calculation_date" column in the database table.
     */
    @Id
    @Column(name = "calculation_date")
    private LocalDate calculationDate;

    /**
     * Represents the phase of the Moon as an enumerated type.
     *
     * This field is mapped to the "phase_enum" column in the "moon_global_data" database table
     * and determines the Moon's phase for a specific calculation date (e.g., NEW_MOON, FULL_MOON).
     *
     * The enumeration values are stored as their string representation in the database.
     *
     * Constraints:
     * - Non-null: This field is mandatory and cannot be null.
     *
     * Annotations:
     * - @Enumerated(EnumType.STRING): Specifies that the enumeration constants are stored as strings in the database.
     * - @Column(name = "phase_enum", nullable = false): Configures the column associated with this field.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "phase_enum", nullable = false)
    private MoonPhaseType phaseEnum;

    /**
     * Represents the localized name of the Moon phase in Polish.
     *
     * This field is mapped to the "phase_moon_pl" column in the "moon_global_data" table
     * and stores the Polish translation of the current Moon phase description, such as "Pełnia" for full moon.
     *
     * Constraints:
     * - Length: Maximum 50 characters.
     * - Not-null: This field is mandatory and cannot be null.
     */
    @Column(name = "phase_moon_pl", length = 50, nullable = false)
    private String phaseMoonPl;

    /**
     * Represents the percentage of the Moon's surface that is illuminated.
     *
     * This field is mapped to the "illumination_pct" column in the "moon_global_data" database table.
     * The value is stored as a BigDecimal with a precision of 5 and a scale of 2,
     * allowing it to capture the illumination percentage with two decimal places.
     * It is a mandatory field and cannot be null.
     */
    @Column(name = "illumination_pct",precision = 5, scale = 2, nullable = false)
    private BigDecimal illuminationPct;

    /**
     * Represents the age of the Moon in days, ranging from 0 to approximately 29.53 days,
     * which constitutes a complete lunar cycle from the new Moon to the next new Moon.
     *
     * This field is mapped to the "moon_age_days" column in the "moon_global_data" table and
     * provides the age of the Moon for a specific calculation date. The value gives insight
     * into the current phase of the Moon based on the number of days elapsed in its cycle.
     *
     * The value is stored as a BigDecimal with a precision of 4 and a scale of 2, allowing
     * for accurate representation of fractional days. It is mandatory and cannot be null.
     */
    @Column(name = "moon_age_days", precision = 4, scale = 2, nullable = false)
    private BigDecimal moonAgeDays;

    /**
     * Indicates whether the Moon is classified as a "supermoon" on a specific date.
     * A supermoon occurs when the Moon's orbit brings it closest to Earth,
     * appearing larger and brighter than usual.
     *
     * This field is mapped to the "is_super_moon" column in the "moon_global_data" table.
     * It is mandatory and cannot be null.
     */
    @Column(name = "is_super_moon", nullable = false)
    private Boolean isSuperMoon;

    /**
     * Represents the distance from the Earth to the Moon in kilometers.
     * This field is mapped to the "distance_km" column in the "moon_global_data" table.
     *
     * The value captures the distance at a specific date as part of the Moon's calculated global data.
     * It provides insights into the Moon's position relative to the Earth.
     */
    @Column(name = "distance_km")
    private Integer distanceKm;



}
