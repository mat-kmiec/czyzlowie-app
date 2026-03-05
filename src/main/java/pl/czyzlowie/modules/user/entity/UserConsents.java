package pl.czyzlowie.modules.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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
