package pl.czyzlowie.modules.fish.utils;

import org.springframework.data.jpa.domain.Specification;
import pl.czyzlowie.modules.fish.entity.FishSpecies;
import pl.czyzlowie.modules.fish.entity.enums.FishCategory;

public class FishSpecification {

    public static Specification<FishSpecies> hasName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<FishSpecies> hasCategory(FishCategory category) {
        return (root, query, cb) -> {
            if (category == null) return cb.conjunction();
            return cb.equal(root.get("category"), category);
        };
    }
}