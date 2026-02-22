package pl.czyzlowie.modules.fish.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import pl.czyzlowie.modules.fish.entity.enums.FishCategory;

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
    private ActivityCalendar activityCalendar;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String algorithmCustomRulesJson;
}
