package pl.czyzlowie.modules.barometer.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a single pressure measurement point with associated metadata.
 * This interface is used to provide information about atmospheric pressure
 * measured at a specific date and time. It includes the measurement date,
 * the hour of the measurement, and the recorded pressure value.
 */
public interface PressurePoint {
    LocalDate getMeasurementDate();
    Integer getMeasurementHour();
    BigDecimal getPressure();
}
