package pl.czyzlowie.modules.spot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.map.entity.MapSpot;
import pl.czyzlowie.modules.map.entity.SpotType;
import pl.czyzlowie.modules.map.repository.MapSpotRepository;
import pl.czyzlowie.modules.spot.dto.SpotFilterDto;
import pl.czyzlowie.modules.spot.dto.SpotListElementDto;
import pl.czyzlowie.modules.spot.mapper.SpotListMapper;
import pl.czyzlowie.modules.spot.utils.SpotSpecification;

/**
 * SpotListService is a service class responsible for handling operations related to the retrieval
 * of spot data, specifically designed for filtered spot list queries.
 *
 * This service performs the following roles:
 * - Applies filtering criteria, encapsulated in a SpotFilterDto, to query specific spots.
 * - Utilizes a custom Specification to dynamically build queries according to the criteria.
 * - Converts the retrieved spot entities into SpotListElementDto instances for consumption by
 *   higher application layers such as controllers.
 *
 * This class operates in a transactional, read-only manner to ensure that no modifications
 * of data are executed through this service and to improve performance for read-heavy operations.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpotListService {

    private final MapSpotRepository spotRepository;
    private final SpotListMapper spotListMapper;

    /**
     * Retrieves a paginated and filtered list of spots based on the provided filter criteria
     * and pagination details.
     *
     * @param filter the filter criteria for narrowing down the list of spots, encapsulated
     *               in a {@code SpotFilterDto}.
     * @param pageable the pagination information, including page number, size, and sorting options.
     * @return a page of {@code SpotListElementDto} containing summarized information about the spots
     *         that match the filter criteria.
     */
    public Page<SpotListElementDto> getFilteredSpots(SpotFilterDto filter, Pageable pageable) {
        Specification<MapSpot> spec = SpotSpecification.withFilter(filter);

        return spotRepository.findAll(spec, pageable)
                .map(spotListMapper::toDto);
    }
}
