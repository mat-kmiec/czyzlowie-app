package pl.czyzlowie.modules.barometer.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.barometer.dto.PressurePoint;
import pl.czyzlowie.modules.imgw.repository.ImgwSynopDataRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Provides barometer data related to pressure history by interacting with the ImgwSynopDataRepository.
 * This class implements the BarometerDataProvider interface to fetch pressure data for a specified station
 * and time range.
 */
@Component("imgwBarometerProvider")
@RequiredArgsConstructor
public class ImgwBarometerProvider implements BarometerDataProvider {

    private final ImgwSynopDataRepository repository;

    /**
     * Retrieves the pressure history for a specific weather station starting from a given date and time.
     *
     * @param stationId the unique identifier of the station for which the pressure history should be retrieved
     * @param since     the starting date and time from which the pressure data should be retrieved
     * @return a list of pressure points containing the measurement date, hour, and pressure values,
     *         ordered by measurement date and hour in descending order
     */
    @Override
    public List<PressurePoint> getPressureHistory(String stationId, LocalDateTime since) {
        return repository.findPressureHistory(stationId, since.toLocalDate());
    }
}
