package pl.czyzlowie.modules.fish.utils;

import org.springframework.data.jpa.domain.Specification;
import pl.czyzlowie.modules.fish.entity.FishSpecies;
import pl.czyzlowie.modules.fish.entity.enums.FishCategory;

/**
 * The FishSpecification class provides specifications for filtering
 * fish species based on various criteria. These specifications are
 * used to build dynamic queries for retrieving fish species from
 * the database.
 */
public class FishSpecification {

    /**
     * Creates a specification to filter fish species by their name.
     *
     * @param name the name to be used as a filter criterion. If null or blank, no filtering will be applied.
     * @return a specification that filters fish species based on the provided name.
     */
    public static Specification<FishSpecies> hasName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    /**
     * Creates a specification to filter fish species by their category.
     *
     * @param category the fish category used as a filter criterion. If null, no filtering will be applied.
     * @return a specification that filters fish species based on the provided category.
     */
    public static Specification<FishSpecies> hasCategory(FishCategory category) {
        return (root, query, cb) -> {
            if (category == null) return cb.conjunction();
            return cb.equal(root.get("category"), category);
        };
    }
}