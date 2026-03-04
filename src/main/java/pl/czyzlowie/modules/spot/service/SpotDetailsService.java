    package pl.czyzlowie.modules.spot.service;

    import jakarta.persistence.EntityNotFoundException;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import pl.czyzlowie.modules.map.entity.MapSpot;
    import pl.czyzlowie.modules.map.entity.RestrictionSpot;
    import pl.czyzlowie.modules.map.entity.SpotType;
    import pl.czyzlowie.modules.map.repository.MapSpotRepository;
    import pl.czyzlowie.modules.spot.dto.SpotDetailsDto;
    import pl.czyzlowie.modules.spot.mapper.SpotDetailsMapper;

    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class SpotDetailsService {

        private final MapSpotRepository mapSpotRepository;
        private final SpotDetailsMapper spotDetailsMapper;


        /**
         * Retrieves the details of a spot based on its slug and type.
         *
         * @param slug the unique identifier (slug) of the spot
         * @param type the type of the spot (e.g., lake, river, commercial, etc.)
         * @return a {@link SpotDetailsDto} containing the details of the requested spot
         * @throws EntityNotFoundException if the spot with the specified slug and type is not found
         */
        @Transactional(readOnly = true)
        public SpotDetailsDto getSpotDetailsBySlugAndType(String slug, SpotType type) {
            log.debug("Szukam w bazie: slug={}, type={}", slug, type);

            MapSpot spotEntity = mapSpotRepository.findBySlugAndSpotType(slug, type)
                    .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono łowiska"));

            return spotDetailsMapper.mapToDto(spotEntity);
        }
    }
