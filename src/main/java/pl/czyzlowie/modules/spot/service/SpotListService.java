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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpotListService {

    private final MapSpotRepository spotRepository;
    private final SpotListMapper spotListMapper;

    public Page<SpotListElementDto> getFilteredSpots(SpotFilterDto filter, Pageable pageable) {
        Specification<MapSpot> spec = SpotSpecification.withFilter(filter);

        return spotRepository.findAll(spec, pageable)
                .map(spotListMapper::toDto);
    }
}
