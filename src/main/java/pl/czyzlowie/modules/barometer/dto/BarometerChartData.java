package pl.czyzlowie.modules.barometer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataPoint {
        private String time;
        private BigDecimal p;
    }
}
