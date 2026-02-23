package pl.czyzlowie.modules.barometer.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BarometerViewDto {
    private String locationName;
    private BigDecimal currentPressure;
    private String trendText;
    private String trendIcon;
    private String conditionTitle;
    private String conditionDescription;
    private String conditionColorClass;
    private String lastUpdatedTime;
    private Boolean isFrontApproaching;
    private BarometerChartData chartData;
}
