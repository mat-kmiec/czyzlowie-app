package pl.czyzlowie.modules.barometer.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) representing the view model for barometer data.
 * This class is used to encapsulate and transport information related to
 * atmospheric pressure and related conditions for a specific location.
 *
 * The encapsulated information includes:
 * - Location name where the barometer readings are taken.
 * - Current atmospheric pressure at the location.
 * - Trend data, including a descriptive text and an icon representing the trend.
 * - Current weather condition details such as the title, description, and color class.
 * - The last updated time for the barometric readings.
 * - Whether a weather front is approaching.
 * - Historical and forecasted barometric data encapsulated in a chart data object.
 */
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
