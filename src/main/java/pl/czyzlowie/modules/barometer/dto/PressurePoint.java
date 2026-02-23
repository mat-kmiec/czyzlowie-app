package pl.czyzlowie.modules.barometer.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PressurePoint {
    LocalDate getMeasurementDate();
    Integer getMeasurementHour();
    BigDecimal getPressure();
}
