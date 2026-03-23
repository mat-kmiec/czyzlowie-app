package pl.czyzlowie.modules.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents statistical data associated with a user in the system. This entity is mapped to the
 * "user_statistics" table in the database and maintains various performance, activity, and metadata attributes
 * related to the user.
 *
 * Relationships:
 * - user: A one-to-one relationship with the User entity, sharing the same primary key through the @MapsId annotation.
 *
 * Fields:
 * - id: The unique identifier for the UserStatistics entity, which corresponds to the user's id.
 * - user: The associated User entity.
 * - totalCatchCount: The total number of catches recorded for the user. Defaults to 0.
 * - totalWeightKg: The total weight of all catches in kilograms, represented with precision and scale. Defaults to 0.00.
 * - longestFishCm: The length of the longest fish caught by the user, in centimeters. Defaults to 0.
 * - pointsTotal: The total points accumulated by the user. Defaults to 0.
 * - rankPosition: The rank position of the user. May be null if ranking data is not available.
 * - totalLogins: The total number of times the user has logged into the system. Defaults to 0.
 * - profileViews: The total number of profile views for the user. Defaults to 0.
 * - lastLoginAt: The date and time of the user's last login. May be null if the user has not logged in.
 * - lastActiveAt: The date and time the user was last active in the system. May be null if activity data is unavailable.
 * - updatedAt: The timestamp of the last modification to this entity, automatically managed through auditing.
 *
 * Auditing:
 * This entity uses Spring Data JPA's auditing functionality to automatically populate the updatedAt field.
 *
 * Usage:
 * This class is intended to capture and manage statistical data related to a user, including activity logs,
 * performance metrics, and ranking data. It is typically linked to the User entity to provide a detailed view
 * of user performance and engagement within the system.
 */
@Entity
@Table(name = "user_statistics")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatistics {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "total_catch_count")
    @Builder.Default
    private int totalCatchCount = 0;

    @Column(name = "total_weight_kg", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalWeightKg = BigDecimal.ZERO;

    @Column(name = "longest_fish_cm")
    @Builder.Default
    private int longestFishCm = 0;

    @Column(name = "points_total")
    @Builder.Default
    private int pointsTotal = 0;

    @Column(name = "rank_position")
    private Integer rankPosition;

    @Column(name = "total_logins")
    @Builder.Default
    private int totalLogins = 0;

    @Column(name = "profile_views")
    @Builder.Default
    private int profileViews = 0;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}