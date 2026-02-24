package pl.czyzlowie.modules.fish.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.czyzlowie.modules.fish.dto.FishDetailsDto;
import pl.czyzlowie.modules.fish.dto.FishFilterDto;
import pl.czyzlowie.modules.fish.dto.FishListElementDto;
import pl.czyzlowie.modules.fish.entity.FishSpecies;
import pl.czyzlowie.modules.fish.mapper.FishMapper;
import pl.czyzlowie.modules.fish.repository.FishSpeciesRepository;
import pl.czyzlowie.modules.fish.utils.FishSpecification;

/**
 * Service class responsible for managing operations related to fish species.
 * Provides functionality to retrieve paginated and filtered lists of fish species
 * as well as detailed information for a specific fish species.
 */
@Service
@RequiredArgsConstructor
public class FishSpeciesService {

    private final FishSpeciesRepository repository;
    private final FishMapper mapper;

    /**
     * Retrieves a paginated list of fish species filtered based on the specified criteria.
     * The filtering is applied using the provided filter parameters such as name and category.
     *
     * @param filter   an instance of {@code FishFilterDto} containing the filter criteria, such as name and category
     * @param pageable an instance of {@code Pageable} for pagination and sorting information
     * @return a paginated {@code Page} of {@code FishListElementDto} objects that match the filter criteria
     */
    @Transactional(readOnly = true)
    public Page<FishListElementDto> getFilteredFish(FishFilterDto filter, Pageable pageable) {
        Specification<FishSpecies> spec = Specification.where(FishSpecification.hasName(filter.getName()))
                .and(FishSpecification.hasCategory(filter.getCategory()));

        return repository.findAll(spec, pageable)
                .map(mapper::toListDto);
    }

    /**
     * Retrieves the details of a fish species based on the given slug.
     * This method fetches data from a cache if available or from the
     * repository if the cache is not populated. If no fish species is
     * found for the given slug, an exception is thrown.
     *
     * @param slug the unique identifier (slug) of the fish species
     * @return a {@code FishDetailsDto} containing detailed information
     *         about the fish species
     * @throws ResponseStatusException if the fish species with the given slug
     *         is not found in the database
     */
    @Cacheable(value = "fishDetails", key = "#slug")
    @Transactional(readOnly = true)
    public FishDetailsDto getFishDetailsDto(String slug) {
        return repository.findBySlug(slug)
                .map(mapper::toDetailsDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono ryby:" + slug));
    }
}