package pl.czyzlowie.modules.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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