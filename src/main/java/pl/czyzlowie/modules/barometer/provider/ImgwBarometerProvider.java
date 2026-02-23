package pl.czyzlowie.modules.barometer.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.barometer.dto.PressurePoint;
import pl.czyzlowie.modules.imgw.repository.ImgwSynopDataRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component("imgwBarometerProvider")
@RequiredArgsConstructor
public class ImgwBarometerProvider implements BarometerDataProvider {

    private final ImgwSynopDataRepository repository;

    @Override
    public List<PressurePoint> getPressureHistory(String stationId, LocalDateTime since) {
        return repository.findPressureHistory(stationId, since.toLocalDate());
    }
}
