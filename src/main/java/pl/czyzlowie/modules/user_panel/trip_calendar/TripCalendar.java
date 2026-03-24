package pl.czyzlowie.modules.user_panel.trip_calendar;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.czyzlowie.modules.user.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a calendar entry for a trip, used to plan and manage fishing trips.
 * This entity is mapped to the "trip_calendar" table in the database and includes details
 * such as trip name, location, timing, fishing method, and additional notes.
 *
 * Relationships:
 * - user: A many-to-one relationship with the User entity, representing the user who created the trip.
 *
 * Fields:
 * - id: Unique identifier for each trip calendar entry.
 * - user: The user associated with this trip.
 * - name: The name of the trip.
 * - location: Location of the trip.
 * - latitude: Latitude coordinates of the trip location.
 * - longitude: Longitude coordinates of the trip location.
 * - startDate: Start date and time of the trip.
 * - endDate: End date and time of the trip.
 * - method: The fishing method used during the trip, based on the FishingMethod enum.
 * - team: Names of team members participating, if applicable.
 * - notes: Additional notes or details about the trip.
 * - createdAt: Timestamp denoting when this entry was created. This field is not updatable.
 * - updatedAt: Timestamp denoting the last modification of this entry.
 *
 * Auditing:
 * This entity uses Spring Data JPA auditing to automatically populate createdAt and updatedAt fields.
 */
@Entity
@Table(name = "trip_calendar")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(precision = 10, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 6)
    private BigDecimal longitude;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FishingMethod method;

    @Column
    private String team;

    @Column(length = 1000)
    private String notes;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
