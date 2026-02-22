package pl.czyzlowie.modules.spot.utils;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import pl.czyzlowie.modules.map.entity.MapSpot;
import pl.czyzlowie.modules.map.entity.SpotType;
import pl.czyzlowie.modules.spot.dto.SpotFilterDto;

import java.util.ArrayList;
import java.util.List;

public class SpotSpecification {

    public static Specification<MapSpot> withFilter(SpotFilterDto filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();


            predicates.add(cb.notEqual(root.get("spotType"), SpotType.RESTRICTION));
            if (filter.getName() != null && !filter.getName().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase() + "%"));
            }

            if (filter.getProvince() != null && !filter.getProvince().isBlank()) {
                predicates.add(cb.equal(root.get("province"), filter.getProvince()));
            }

            if (filter.getSpotType() != null) {
                predicates.add(cb.equal(root.get("spotType"), filter.getSpotType()));
            }

            if (filter.getNearestCity() != null && !filter.getNearestCity().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("nearestCity")), "%" + filter.getNearestCity().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
