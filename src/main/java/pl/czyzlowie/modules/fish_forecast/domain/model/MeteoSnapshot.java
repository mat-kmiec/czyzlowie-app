package pl.czyzlowie.modules.fish_forecast.domain.model;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a single snapshot of high-frequency meteorological data.
 * This object maps to highly granular data (e.g., 10-minute intervals) typically
 * provided by automated IMGW meteo stations.
 * * Unlike Synop data which gives a broader weather overview, Meteo data provides
 * micro-level insights such as ground temperature and short-burst precipitation,
 * which are highly valuable for predicting fish behavior in shallow waters
 * or during sudden weather shifts (e.g., summer storms).
 *
 * @param timestamp          The unified logical time of this snapshot (usually aligned to 10 or 30-min intervals).
 * @param airTemperature     Air temperature in degrees Celsius.
 * @param groundTemperature  Ground temperature in degrees Celsius. Crucial for shallow-water spring fishing predictions.
 * @param windDirection      Wind direction in meteorological degrees (0-360).
 * @param windAverageSpeed   Average wind speed over the measurement interval.
 * @param windMaxSpeed       Maximum wind speed recorded during the interval.
 * @param windGust           Maximum wind gust speed (e.g., 10-minute maximum). Important for boat fishing safety.
 * @param humidity           Relative air humidity percentage (0-100).
 * @param precipitation10min Precipitation volume measured over a 10-minute window. Detects sudden downpours.
 */
@Builder
public record MeteoSnapshot(
        LocalDateTime timestamp,

        // --- Temperatures ---
        BigDecimal airTemperature,
        BigDecimal groundTemperature,

        // --- Wind Metrics ---
        Integer windDirection,
        BigDecimal windAverageSpeed,
        BigDecimal windMaxSpeed,
        BigDecimal windGust,

        // --- Moisture & Precipitation ---
        BigDecimal humidity,
        BigDecimal precipitation10min
) {}
