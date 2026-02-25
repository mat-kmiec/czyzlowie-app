package pl.czyzlowie.modules.imgw_ui.hydro.dto;

import lombok.Builder;
import lombok.Data;
import pl.czyzlowie.modules.imgw_ui.hydro.dto.HydroReadingDto;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class HydroDashboardDto {
    private String locationName;
    private String stationId;
    private String stationName;
    private double distanceKm;

    private HydroReadingDto currentReading;
    private Trend waterLevelTrend;
    private String lastWaterLevelTime;
    private String lastDischargeTime;
    private String lastTemperatureTime;
    private boolean waterLevelStale;
    private boolean dischargeStale;
    private boolean temperatureStale;

    private List<HydroReadingDto> history;
    private List<String> chartLabels;
    private List<String> chartTimestampsIso;
    private List<Integer> chartWaterLevels;
    private List<BigDecimal> chartDischarges;
    private List<BigDecimal> chartWaterTemperatures;

    private boolean hasWaterLevelData;
    private boolean hasDischargeData;
    private boolean hasTemperatureData;

    public enum Trend {
        RISING, FALLING, STABLE, UNKNOWN
    }
}