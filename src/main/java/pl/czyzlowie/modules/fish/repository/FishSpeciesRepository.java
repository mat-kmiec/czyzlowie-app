package pl.czyzlowie.modules.fish.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.fish.entity.FishSpecies;

import java.util.Optional;

/**
 * Repository interface for managing {@code FishSpecies} entities.
 * Extends {@code JpaRepository} to provide standard CRUD operations
 * and {@code JpaSpecificationExecutor} for dynamic query execution.
 */
@Repository
public interface FishSpeciesRepository extends JpaRepository<FishSpecies, Long>, JpaSpecificationExecutor<FishSpecies> {

    /**
     * Finds a fish species by its unique slug identifier. The query
     * is enhanced with an EntityGraph to fetch specified attributes
     * for optimized performance while minimizing additional queries.
     *
     * @param slug the unique slug identifier of the fish species
     * @return an {@code Optional} containing the {@code FishSpecies}
     *         if found; otherwise an empty {@code Optional}
     */
    @EntityGraph(attributePaths = {
            "algorithmParams",
            "habitat",
            "pzwRegulations",
            "tackleSetup",
            "polishRecord",
            "activityCalendar"
    })
    Optional<FishSpecies> findBySlug(String slug);
}
