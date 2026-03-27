package pl.czyzlowie.modules.spot.utils;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import pl.czyzlowie.modules.map.entity.MapSpot;
import pl.czyzlowie.modules.map.entity.SpotType;
import pl.czyzlowie.modules.spot.dto.SpotFilterDto;

import java.util.ArrayList;
import java.util.List;

/**
 * The SpotSpecification class provides a dynamic method for creating query specifications
 * to filter {@code MapSpot} entities based on various criteria encapsulated in {@code SpotFilterDto}.
 * It enables precise and flexible querying of database records by constructing a {@code Specification}.
 */
public class SpotSpecification {

    /**
     * Creates a dynamic query specification for filtering {@code MapSpot} entities based on a given
     * {@code SpotFilterDto}. This method builds a {@code Specification} to apply filtering conditions
     * to database queries, enabling flexibility in fetching data according to the provided criteria.
     *
     * @param filter an instance of {@code SpotFilterDto} containing the filter criteria such as name,
     * province, spot type, and nearest city to narrow down the search results.
     * @return a {@code Specification<MapSpot>} instance that can be used to query the database for
     * spots matching the given filter criteria.
     */
    public static Specification<MapSpot> withFilter(SpotFilterDto filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getSpotType() != null) {
                predicates.add(cb.equal(root.get("spotType"), filter.getSpotType()));
            } else {
                List<SpotType> excludedTypes = List.of(
                        SpotType.RESTRICTION,
                        SpotType.SLIP,
                        SpotType.FISHING_SHOP,
                        SpotType.RENTALS
                );
                predicates.add(cb.not(root.get("spotType").in(excludedTypes)));
            }

            // 2. Reszta filtrów
            if (filter.getName() != null && !filter.getName().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase() + "%"));
            }

            if (filter.getProvince() != null && !filter.getProvince().isBlank()) {
                predicates.add(cb.equal(root.get("province"), filter.getProvince()));
            }

            if (filter.getNearestCity() != null && !filter.getNearestCity().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("nearestCity")), "%" + filter.getNearestCity().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}