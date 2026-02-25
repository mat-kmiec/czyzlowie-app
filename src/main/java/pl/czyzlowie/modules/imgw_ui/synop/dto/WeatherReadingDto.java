package pl.czyzlowie.modules.imgw_ui.synop.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WeatherReadingDto {
    private String timeLabel;
    private String timestamp;
    private BigDecimal temperature;
    private BigDecimal pressure;
    private BigDecimal windSpeed;
    private Integer windDirection;
    private BigDecimal humidity;
    private BigDecimal precipitation;
}