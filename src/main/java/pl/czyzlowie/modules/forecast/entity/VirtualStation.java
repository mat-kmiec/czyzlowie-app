package pl.czyzlowie.modules.forecast.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Represents a virtual weather station entity.
 * This class is mapped to the table "virtual_stations" in the database.
 * It contains information about the virtual station, including location,
 * name, activity status, and an identifier.
 *
 * Fields:
 * - id: The unique identifier of the station, limited to 20 characters.
 * - name: The name of the virtual station, non-null.
 * - latitude: The latitude coordinate of the station, non-null with precision of 10 and scale of 8.
 * - longitude: The longitude coordinate of the station, non-null with precision of 10 and scale of 8.
 * - active: Indicates whether the virtual station is active. Defaults to true.
 *
 * The VirtualStation class is utilized in various relationships across the system,
 * including weather data associations, forecasts, and usage in import logs.
 */
@Entity
@Table(name = "virtual_stations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VirtualStation {

    @Id
    @Column(length = 20)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 10, scale = 8)
    private BigDecimal longitude;

    @Column(name = "is_active")
    @Builder.Default
    private boolean active = true;
}