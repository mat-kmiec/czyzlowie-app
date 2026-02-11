package pl.czyzlowie.modules.moon.entity;

import jakarta.persistence.*;
import lombok.*;
import pl.czyzlowie.modules.moon.entity.Enums.MoonPhaseType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "moon_global_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoonGlobalData {

    @Id
    @Column(name = "calculation_date")
    private LocalDate calculationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "phase_enum", nullable = false)
    private MoonPhaseType phaseEnum;

    @Column(name = "phase_moon_pl", length = 50, nullable = false)
    private String phaseMoonPl;

    @Column(name = "illumination_pct",precision = 5, scale = 2, nullable = false)
    private BigDecimal illuminationPct;

    @Column(name = "moon_age_days", precision = 4, scale = 2, nullable = false)
    private BigDecimal moonAgeDays;

    @Column(name = "is_super_moon", nullable = false)
    private Boolean isSuperMoon;

    @Column(name = "distance_km")
    private Integer distanceKm;



}
