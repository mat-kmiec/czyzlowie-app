package pl.czyzlowie.modules.fish.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.fish.dto.FishDetailsDto;
import pl.czyzlowie.modules.fish.dto.FishFilterDto;
import pl.czyzlowie.modules.fish.dto.FishListElementDto;
import pl.czyzlowie.modules.fish.entity.FishSpecies;
import pl.czyzlowie.modules.fish.mapper.FishMapper;
import pl.czyzlowie.modules.fish.repository.FishSpeciesRepository;
import pl.czyzlowie.modules.fish.utils.FishSpecification;

@Service
@RequiredArgsConstructor
public class FishSpeciesService {

    private final FishSpeciesRepository repository;
    private final FishMapper mapper;

    @Transactional(readOnly = true)
    public Page<FishListElementDto> getFilteredFish(FishFilterDto filter, Pageable pageable) {
        Specification<FishSpecies> spec = Specification.where(FishSpecification.hasName(filter.getName()))
                .and(FishSpecification.hasCategory(filter.getCategory()));

        return repository.findAll(spec, pageable)
                .map(mapper::toListDto);
    }

    @Transactional(readOnly = true)
    public FishDetailsDto getFishDetailsDto(String slug) {
        return repository.findBySlug(slug)
                .map(mapper::toDetailsDto)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono ryby o podanym adresie: " + slug));
    }
}