package pl.czyzlowie.modules.imgw_ui.meteo.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class MeteoReadingDto {
    private LocalDateTime timestamp;
    private String timeLabel;
    private String timestampIso;

    private BigDecimal airTemp;
    private LocalDateTime airTempDate;

    private BigDecimal windAvgSpeed;
    private BigDecimal windMaxSpeed;
    private Integer windDirection;
    private LocalDateTime windMeasurementTime;

    private BigDecimal precipitation10min;
    private LocalDateTime precipitation10minTime;

    private BigDecimal relativeHumidity;
}