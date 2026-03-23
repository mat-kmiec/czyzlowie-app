package pl.czyzlowie.modules.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Represents the consent preferences of a user.
 * This entity is used to record the consents that a user has provided, including acceptance of terms, newsletters, and marketing communications.
 *
 * An instance of this class is linked to a specific {@link User}, and each consent record corresponds to a single user.
 *
 * The consents include:
 * - Acceptance of terms and conditions.
 * - Version of the terms accepted.
 * - Acceptance of receiving newsletters.
 * - Acceptance of receiving marketing communications.
 *
 * The date and time of consent are recorded upon entity creation.
 *
 * This entity is audited automatically, and it requires a database table named "user_consents".
 */
@Entity
@Table(name = "user_consents")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserConsents {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "terms_accepted", nullable = false)
    @Builder.Default
    private boolean termsAccepted = false;

    @Column(name = "terms_version", length = 20)
    private String termsVersion;

    @Column(name = "newsletter_accepted")
    @Builder.Default
    private boolean newsletterAccepted = false;

    @Column(name = "marketing_accepted")
    @Builder.Default
    private boolean marketingAccepted = false;

    @CreatedDate
    @Column(name = "consented_at", updatable = false)
    private LocalDateTime consentedAt;
}
