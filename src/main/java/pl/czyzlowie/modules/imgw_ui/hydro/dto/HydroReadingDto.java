package pl.czyzlowie.modules.imgw_ui.hydro.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class HydroReadingDto {
    private LocalDateTime timestamp;
    private String timeLabel;
    private String timestampIso;

    private Integer waterLevel;
    private LocalDateTime waterLevelDate;

    private BigDecimal waterTemperature;
    private LocalDateTime waterTemperatureDate;

    private BigDecimal discharge;
    private LocalDateTime dischargeDate;

    private Integer icePhenomenon;
}
