package pl.czyzlowie.modules.barometer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.barometer.dto.BarometerViewDto;
import pl.czyzlowie.modules.barometer.entity.StationBarometerId;
import pl.czyzlowie.modules.barometer.entity.StationBarometerStats;
import pl.czyzlowie.modules.barometer.mapper.BarometerViewMapper;
import pl.czyzlowie.modules.barometer.repository.StationBarometerStatsRepository;

@Service
@RequiredArgsConstructor
public class BarometerViewService {

    private final StationBarometerStatsRepository repository;
    private final BarometerViewMapper mapper;
    private final LocationFinderService locationFinder;

    @Transactional(readOnly = true)
    public BarometerViewDto getBarometerDataForView(Double lat, Double lon, String locationName) {

        if (lat == null || lon == null) {
            lat = 52.2297;
            lon = 21.0122;
            locationName = "Warszawa";
        }

        LocationFinderService.NearestStation nearest =
                locationFinder.findNearestStation(lat, lon, StationCategory.SYNOPTIC);

        StationBarometerId id = new StationBarometerId(nearest.stationId(), nearest.type());

        StationBarometerStats stats = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brak przeliczonych statystyk dla najbliższej stacji: " + nearest.stationId()));

        return mapper.toDto(stats, locationName);
    }
}
