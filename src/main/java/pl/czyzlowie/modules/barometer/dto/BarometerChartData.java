package pl.czyzlowie.modules.barometer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Represents the data required to generate a barometer chart. This class encapsulates
 * historical and forecasted atmospheric pressure data over various timeframes.
 *
 * The encapsulated data includes:
 * - Historical data for the past 24 hours, 3 days, and 5 days.
 * - Forecasted data for the next 24 hours and 3 days.
 *
 * Each dataset consists of a collection of DataPoint objects, which provide
 * individual pressure readings at specific timestamps.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BarometerChartData {
    private List<DataPoint> history24h;
    private List<DataPoint> history3d;
    private List<DataPoint> history5d;
    private List<DataPoint> forecast24h;
    private List<DataPoint> forecast3d;

    /**
     * Represents a data point containing a timestamp and an associated pressure value.
     * This class is primarily used to model individual elements of pressure data,
     * which can be utilized in both historical and forecasted atmospheric pressure datasets.
     *
     * Each DataPoint instance consists of the following attributes:
     * - time: A string representation of the timestamp associated with the data point.
     * - p: A BigDecimal representation of the atmospheric pressure value.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataPoint {
        private String time;
        private BigDecimal p;
    }
}
