package pl.czyzlowie.modules.user_panel.catch_log;

import jakarta.persistence.*;
import lombok.*;
import pl.czyzlowie.modules.user.entity.User; // Twoja encja użytkownika

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a fishing catch record associated with a user.
 * This class is mapped to the "catch_records" table in the database and contains
 * details about a specific catch event such as its location, environmental conditions, and
 * other relevant metadata.
 *
 * An instance of this class is linked to a specific user via a many-to-one relationship.
 * It also includes various environmental attributes such as air temperature, wind speed,
 * and water temperature for documenting the conditions during the catch.
 *
 * The creation date of a record is automatically set using the {@code @PrePersist} lifecycle
 * callback, ensuring that this value is always populated upon insertion into the database.
 */
@Entity
@Table(name = "catch_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatchRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "catch_date", nullable = false)
    private LocalDateTime catchDate;

    @Column(name = "location_name", nullable = false)
    private String locationName;

    @Column(precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(nullable = false, length = 50)
    private String species;

    @Column(precision = 5, scale = 2)
    private BigDecimal weight;

    private Integer length;

    @Column(name = "lure_method")
    private String lureMethod;

    @Column(length = 500)
    private String note;

    @Column(name = "ignore_hydro", nullable = false)
    private boolean ignoreHydro;

    @Column(name = "ignore_telemetry", nullable = false)
    private boolean ignoreTelemetry;

    @Column(name = "air_temperature", precision = 4, scale = 1)
    private BigDecimal airTemperature;

    @Column(precision = 6, scale = 1)
    private BigDecimal pressure;

    @Column(name = "moon_phase", length = 50)
    private String moonPhase;

    @Column(name = "water_level")
    private Integer waterLevel;

    @Column(name = "water_temperature", precision = 4, scale = 1)
    private BigDecimal waterTemperature;

    @Column(precision = 4, scale = 1)
    private BigDecimal humidity;

    @Column(precision = 5, scale = 2)
    private BigDecimal precipitation;

    @Column(name = "wind_speed", precision = 4, scale = 1)
    private BigDecimal windSpeed;

    @Column(name = "wind_direction", length = 50)
    private String windDirection;

    @Column(precision = 8, scale = 3)
    private BigDecimal discharge;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}