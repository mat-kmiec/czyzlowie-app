package pl.czyzlowie.modules.barometer.entity;

/**
 * Represents the trend in barometric pressure changes.
 *
 * The PressureTrend enumeration categorizes the changes in pressure into different trends
 * based on the rate of change over time. It is used to indicate whether the pressure
 * is rising, falling, or stable, and the relative speed of these changes.
 *
 * This enumeration is commonly used in weather data analysis and forecasts
 * to provide insights about atmospheric conditions.
 *
 * Enum Constants:
 * - RISING_FAST: Indicates that the barometric pressure is increasing rapidly.
 * - RISING: Indicates that the barometric pressure is increasing steadily.
 * - STABLE: Indicates that the barometric pressure is relatively constant.
 * - FALLING: Indicates that the barometric pressure is decreasing steadily.
 * - FALLING_FAST: Indicates that the barometric pressure is decreasing rapidly.
 */
public enum PressureTrend {
    RISING_FAST,
    RISING,
    STABLE,
    FALLING,
    FALLING_FAST
}