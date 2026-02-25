package pl.czyzlowie.modules.imgw_ui.meteo.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class MeteoDashboardDto {
    private String locationName;
    private String stationId;
    private String stationName;
    private double distanceKm;

    private MeteoReadingDto currentReading;
    private Trend tempTrend;
    private String lastTempTime;
    private String lastWindTime;
    private String lastPrecipTime;
    private boolean tempStale;
    private boolean windStale;

    private List<MeteoReadingDto> history;
    private List<String> chartLabels;
    private List<String> chartTimestampsIso;
    private List<BigDecimal> chartAirTemps;
    private List<BigDecimal> chartWindSpeeds;
    private List<BigDecimal> chartWindMaxSpeeds;
    private List<BigDecimal> chartPrecipitation;
    private boolean hasTempData;
    private boolean hasWindData;
    private boolean hasPrecipitationData;

    public enum Trend {
        RISING, FALLING, STABLE, UNKNOWN
    }
}