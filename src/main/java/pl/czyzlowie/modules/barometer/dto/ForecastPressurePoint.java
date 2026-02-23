package pl.czyzlowie.modules.barometer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ForecastPressurePoint {
    LocalDateTime getForecastTime();
    BigDecimal getPressure();
}