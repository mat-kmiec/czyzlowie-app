package pl.czyzlowie.modules.barometer.provider;

import pl.czyzlowie.modules.barometer.dto.PressurePoint;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The {@code BarometerDataProvider} interface defines methods to retrieve barometric pressure data
 * for specified weather stations. It is designed to provide historical barometric measurements
 * filtered by the weather station identifier and starting date-time.
 */
public interface BarometerDataProvider {
    /**
     * Retrieves the pressure history for a specific weather station starting from a given date and time.
     *
     * @param stationId the unique identifier of the weather station
     *                  for which the pressure history is to be retrieved
     * @param since     the starting date and time from which to retrieve pressure data
     * @return a list of {@code PressurePoint} objects, which contain the measurement date,
     *         measurement hour, and pressure values, ordered by date and hour in descending order
     */
    List<PressurePoint> getPressureHistory(String stationId, LocalDateTime since);
}