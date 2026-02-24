package pl.czyzlowie.modules.barometer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a forecast data point specifically for atmospheric pressure.
 * This interface is utilized to provide information about predicted atmospheric
 * pressure at a specific time in the future.
 *
 * The following details are available for each forecast point:
 * - Forecast Time: The exact date and time for which the pressure is forecasted.
 * - Pressure: The forecasted atmospheric pressure value in hectopascals (hPa).
 */
public interface ForecastPressurePoint {
    LocalDateTime getForecastTime();
    BigDecimal getPressure();
}