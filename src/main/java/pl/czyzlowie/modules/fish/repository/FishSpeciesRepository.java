package pl.czyzlowie.modules.fish.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.fish.entity.FishSpecies;

import java.util.Optional;

@Repository
public interface FishSpeciesRepository extends JpaRepository<FishSpecies, Long>, JpaSpecificationExecutor<FishSpecies> {
    @EntityGraph(attributePaths = {"algorithmParams"})
    Optional<FishSpecies> findBySlug(String slug);
}
