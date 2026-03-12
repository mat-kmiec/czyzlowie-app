package pl.czyzlowie.modules.user_panel.favorite_spots;

import jakarta.persistence.*;
import lombok.*;
import pl.czyzlowie.modules.user.entity.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user's favorite fishing spot.
 * This entity is mapped to a "favorite_spots" table in the database.
 */
@Entity
@Table(name = "favorite_spots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(name = "location_display")
    private String locationDisplay;

    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "water_type", nullable = false)
    private WaterType waterType;

    @ElementCollection
    @CollectionTable(name = "spot_fish_tags", joinColumns = @JoinColumn(name = "spot_id"))
    @Column(name = "tag", nullable = false)
    @Builder.Default
    private List<String> fishTags = new ArrayList<>();


    @Column(length = 500)
    private String note;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}