package pl.czyzlowie.modules.imgw_ui.synop.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class SynopDashboardDto {
    private String locationName;
    private String stationType;
    private double distanceKm;
    private LocalDate selectedDate;
    private WeatherReadingDto currentReading;
    private List<WeatherReadingDto> dailyHistory;
    private List<String> chartLabels;
    private List<BigDecimal> chartTemperatures;
    private List<BigDecimal> chartPressures;
    private List<BigDecimal> chartWindSpeeds;
    private List<BigDecimal> chartPrecipitation;
    private List<String> chartTimestamps;

}
