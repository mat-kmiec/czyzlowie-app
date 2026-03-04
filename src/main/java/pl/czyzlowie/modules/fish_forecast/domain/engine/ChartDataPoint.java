package pl.czyzlowie.modules.fish_forecast.domain.engine;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Represents a data point in a chart containing a timestamp and a corresponding numeric value.
 *
 * This class is immutable and uses the record feature to automatically generate constructors,
 * accessors, equals, hashCode, and toString methods.
 *
 * It is intended for use in charting or time-series data representations where each data point
 * is associated with a specific timestamp.
 *
 * The class is created using the Builder pattern to facilitate the creation of instances
 * while providing flexibility and readability.
 *
 * Components:
 * - timestamp: The date and time representing when the data point occurred.
 * - value: The numeric value associated with the specified timestamp.
 */
@Builder
public record ChartDataPoint(
        LocalDateTime timestamp,
        double value
) {}