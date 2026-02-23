package pl.czyzlowie.modules.barometer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.barometer.dto.BarometerViewDto;
import pl.czyzlowie.modules.barometer.entity.StationBarometerId;
import pl.czyzlowie.modules.barometer.entity.StationBarometerStats;
import pl.czyzlowie.modules.barometer.entity.StationType;
import pl.czyzlowie.modules.barometer.mapper.BarometerViewMapper;
import pl.czyzlowie.modules.barometer.repository.StationBarometerStatsRepository;

@Service
@RequiredArgsConstructor
public class BarometerViewService {

    private final StationBarometerStatsRepository repository;
    private final BarometerViewMapper mapper;

    @Transactional(readOnly = true)
    public BarometerViewDto getBarometerDataForView(String searchQuery) {
        StationBarometerId testId = new StationBarometerId("12295", StationType.IMGW_SYNOP);

        StationBarometerStats stats = repository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Brak danych statystycznych dla stacji testowej"));

        return mapper.toDto(stats, "Stacja Testowa (Symulator)");
    }
}
