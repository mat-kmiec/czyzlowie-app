package pl.czyzlowie.modules.fish_forecast.domain.model;

import lombok.Builder;
import java.util.List;

/**
 * WeatherContext serves as the primary data aggregate for the fish activity prediction engine.
 * It encapsulates unified and "stitched" time-series data from multiple sources
 * (IMGW, OpenMeteo, and Astronomical databases).
 *
 * The data structure is designed as a continuous timeline, typically covering a range
 * from 72 hours in the past to 72 hours in the future relative to the forecast target time.
 *
 * @param synopTimeline Chronological list of synoptic data. Includes core atmospheric
 * parameters such as pressure, air temperature, and wind metrics.
 * Unified for both physical (IMGW) and virtual stations.
 *
 * @param hydroTimeline Time-series of hydrological parameters. Covers water temperature,
 * water levels, ice phenomena, and vegetation overgrowth.
 * Crucial for river-based fishing forecasts.
 *
 * @param meteoTimeline High-frequency meteorological data (typically 30-min intervals).
 * Contains precision rainfall, humidity, and supplemental parameters
 * (e.g., cloud cover, UV index) often enriched by numerical weather models.
 *
 * @param moonTimeline  Chronological astronomical data (typically daily intervals).
 * Provides moon phases, illumination percentage, and precise
 * sunrise/sunset and moonrise/moonset times.
 */
@Builder
public record WeatherContext(
        List<SynopSnapshot> synopTimeline,
        List<HydroSnapshot> hydroTimeline,
        List<MeteoSnapshot> meteoTimeline,
        List<MoonSnapshot> moonTimeline
) {
    /**
     * Canonical constructor for data integrity and thread safety.
     * It ensures that all timelines are immutable and non-null, preventing
     * NullPointerExceptions during parallel rule processing in the calculation engine.
     */
    public WeatherContext {
        synopTimeline = synopTimeline != null ? List.copyOf(synopTimeline) : List.of();
        hydroTimeline = hydroTimeline != null ? List.copyOf(hydroTimeline) : List.of();
        meteoTimeline = meteoTimeline != null ? List.copyOf(meteoTimeline) : List.of();
        moonTimeline = moonTimeline != null ? List.copyOf(moonTimeline) : List.of();
    }
}