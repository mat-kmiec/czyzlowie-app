package pl.czyzlowie.modules.fish.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import pl.czyzlowie.modules.fish.entity.enums.FishCategory;


/**
 * Represents a specific fish species within the fish atlas module.
 *
 * The FishSpecies entity is the central point of the fish-related data model. It aggregates
 * general information, biological classification, habitat requirements, angling techniques,
 * and legal regulations for a given species.
 * * It also holds technical parameters used by forecasting algorithms and lunar phase
 * calculations to determine the best fishing times.
 *
 * Key components include:
 * - Basic identification: Name (Polish, Latin, English) and URL-friendly slug.
 * - Biological & Habitat data: Category (predator/peaceful) and preferred living conditions.
 * - Angling context: Recommended tackle setup, PZW regulations, and record catches in Poland.
 * - Temporal data: Activity calendar across months.
 * - Algorithm data: Detailed parameters for weather-based activity prediction and custom JSON rules.
 *
 * This entity uses a mix of standard columns, embedded objects ({@code @Embedded}),
 * and a one-to-one relationship with algorithm parameters.
 */
@Entity
@Table(name = "fish_species")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FishSpecies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "latin_name")
    private String latinName;

    @Column(name = "english_name")
    private String englishName;

    @Column(nullable = false, unique = true)
    private String slug;

    private String imgUrl;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "algorithm_params_id")
    private FishAlgorithmParams algorithmParams;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FishCategory category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Embedded
    private FishHabitat habitat;

    @Embedded
    private TackleSetup tackleSetup;

    @Embedded
    private PolishRecord polishRecord;

    @Embedded
    private PzwRegulations pzwRegulations;

    @Embedded
    private ActivityCalendar activityCalendar;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String algorithmCustomRulesJson;
}
