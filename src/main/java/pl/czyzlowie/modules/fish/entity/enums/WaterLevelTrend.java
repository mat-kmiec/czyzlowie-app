package pl.czyzlowie.modules.fish.entity.enums;

/**
 * Represents the directional change in water levels and its impact on fish behavior.
 *
 * This enum is used by the forecasting algorithm to determine how a species reacts
 * to hydrological changes. Many species change their feeding patterns or move
 * to different depths based on whether the water is rising, falling, or remaining constant.
 *
 * Values:
 * - RISING: Increasing water levels, often associated with increased turbidity or flooding.
 * - FALLING: Receding water levels, which may concentrate fish in deeper areas.
 * - STABLE: Constant water levels, representing a lack of significant hydrological change.
 * - ANY: Indicates the species' activity is not significantly influenced by water level trends.
 */
public enum WaterLevelTrend {
    RISING,
    FALLING,
    STABLE,
    ANY
}
