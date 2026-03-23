package pl.czyzlowie.modules.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Represents a user entity within the system. This class is mapped to the "users" table in the database
 * and uses JPA annotations to define the structure and relationships with other entities.
 *
 * The User class contains attributes that uniquely identify a user and manage their authentication, permissions,
 * and state within the system. It also maintains relationships with associated entities, such as user consents
 * and user statistics. Auditing fields are included for tracking creation and modification times.
 *
 * Fields:
 * - id: Unique identifier for the user.
 * - email: The email address of the user. Must be unique and not null.
 * - username: The username of the user. Must be unique, not null, and have a maximum length of 50.
 * - passwordHash: The hashed password of the user.
 * - provider: Enum representing the authentication provider for the user.
 * - providerId: Identifier associated with the user's authentication provider.
 * - role: Enum representing the role assigned to the user.
 * - isEnabled: Indicates whether the user account is active or temporarily disabled. Defaults to true.
 * - isEmailVerified: Indicates whether the user's email has been verified. Defaults to false.
 * - isLocked: Indicates whether the user's account is locked due to security reasons. Defaults to false.
 * - failedLoginAttempts: Tracks the number of failed login attempts by the user. Defaults to 0.
 * - createdAt: Timestamp of when the user entity was created. Not updatable.
 * - updatedAt: Timestamp of when the user entity was last updated.
 * - consents: A one-to-one relationship with UserConsents to manage user consent preferences.
 * - statistics: A one-to-one relationship with UserStatistics to store user activity and performance data.
 *
 * Relationships:
 * - consents: Bi-directional one-to-one mapping with the UserConsents entity. Automatically updates the user reference in UserConsents.
 * - statistics: Bi-directional one-to-one mapping with the UserStatistics entity. Automatically updates the user reference in UserStatistics.
 *
 * Auditing:
 * This entity uses Spring Data JPA's auditing functionality to automatically populate createdAt and updatedAt fields.
 *
 * Additional methods:
 * - setConsents: Sets the UserConsents entity for the user and ensures the bi-directional relationship is maintained.
 * - setStatistics: Sets the UserStatistics entity for the user and ensures the bi-directional relationship is maintained.
 */
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password_hash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider;

    @Column(name = "provider_id")
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(name = "is_enabled")
    @Builder.Default
    private boolean isEnabled = true;

    @Column(name = "is_email_verified")
    @Builder.Default
    private boolean isEmailVerified = false;

    @Column(name = "is_locked")
    @Builder.Default
    private boolean isLocked = false;

    @Column(name = "failed_login_attempts")
    @Builder.Default
    private int failedLoginAttempts = 0;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private UserConsents consents;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private UserStatistics statistics;

    public void setConsents(UserConsents consents) {
        if (consents != null) consents.setUser(this);
        this.consents = consents;
    }

    public void setStatistics(UserStatistics statistics) {
        if (statistics != null) statistics.setUser(this);
        this.statistics = statistics;
    }
}