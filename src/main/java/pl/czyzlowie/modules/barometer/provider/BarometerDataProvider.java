package pl.czyzlowie.modules.barometer.provider;

import pl.czyzlowie.modules.barometer.dto.PressurePoint;

import java.time.LocalDateTime;
import java.util.List;

public interface BarometerDataProvider {
    List<PressurePoint> getPressureHistory(String stationId, LocalDateTime since);
}