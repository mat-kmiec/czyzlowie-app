package pl.czyzlowie.modules.moon.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "moon_data")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class MoonData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "region_node", nullable = false)
    private MoonRegion regionNode;

    private String phaseName;
    private BigDecimal illumination;
    private BigDecimal moonAgeDays;
    private BigDecimal distanceKm;

    private LocalDateTime moonrise;
    private LocalDateTime moonset;
    private LocalDateTime majorPeriodStart;

    private LocalDateTime fetchedAt;
}