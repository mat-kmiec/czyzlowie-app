    package pl.czyzlowie.modules.spot.service;

    import jakarta.persistence.EntityNotFoundException;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import pl.czyzlowie.modules.map.entity.MapSpot;
    import pl.czyzlowie.modules.map.entity.RestrictionSpot;
    import pl.czyzlowie.modules.map.repository.MapSpotRepository;
    import pl.czyzlowie.modules.spot.dto.SpotDetailsDto;
    import pl.czyzlowie.modules.spot.mapper.SpotDetailsMapper;

    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class SpotDetailsService {

        private final MapSpotRepository mapSpotRepository;
        private final SpotDetailsMapper spotDetailsMapper;

        @Transactional(readOnly = true)
        public SpotDetailsDto getSpotDetailsBySlug(String slug) {
            log.debug("Pobieranie szczegółów dla łowiska o slugu: {}", slug);

            MapSpot spotEntity = mapSpotRepository.findBySlug(slug)
                    .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono łowiska o adresie: " + slug));

            if (spotEntity instanceof RestrictionSpot) {
                log.warn("Próba dostępu do strony szczegółów dla restrykcji (slug: {})", slug);
                throw new IllegalArgumentException("Zakazy wędkarskie nie posiadają oddzielnej strony szczegółów.");
            }

            return spotDetailsMapper.mapToDto(spotEntity);
        }
    }
