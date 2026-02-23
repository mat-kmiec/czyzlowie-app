package pl.czyzlowie.modules.forecast.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Represents a response from the OpenMeteo API, containing comprehensive weather data
 * for a specific location based on latitude and longitude coordinates.
 * This class is designed to handle deserialized responses from the API
 * and holds weather information for current, hourly, and daily forecasts.
 *
 * Key attributes of this class include:
 * - Latitude and longitude representing geographic coordinates.
 * - UTC offset in seconds for managing time zone differences.
 * - Timezone string indicating the location's time zone.
 * - A nested object for current weather data (`CurrentDto`).
 * - A nested object for hourly weather data (`HourlyDto`).
 * - A nested object for daily weather data (`DailyDto`).
 *
 * The encapsulated data provides a structured way to access detailed
 * weather forecasting or real-time weather conditions using the data
 * formatted and supplied by the OpenMeteo API.
 * Each nested class corresponds to a specific segment of weather data:
 * real-time, hourly, and daily, with all values mapped from the API's JSON response.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMeteoResponse {

    private Double latitude;
    private Double longitude;

    @JsonProperty("utc_offset_seconds")
    private Integer utcOffsetSeconds;

    @JsonProperty("timezone")
    private String timezone;

    @JsonProperty("current")
    private CurrentDto current;

    @JsonProperty("hourly")
    private HourlyDto hourly;

    @JsonProperty("daily")
    private DailyDto daily;


    /**
     * Represents the current weather data retrieved from an OpenMeteo API response.
     * This class contains various attributes detailing the real-time weather conditions
     * at a specific location.
     *
     * It includes information such as:
     * - The time of the weather data retrieval.
     * - Temperature at a height of 2 meters and the perceived temperature.
     * - Surface pressure in hPa.
     * - Wind-related metrics including speed, gusts, and direction at a height of 10 meters.
     * - Cloud cover percentage.
     * - Rainfall amount in millimeters.
     * - A weather code representing the atmospheric condition.
     *
     * This data is typically intended to provide a detailed snapshot of current meteorological conditions.
     * Attributes are mapped to JSON properties from the API response for deserialization.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurrentDto {
        @JsonProperty("time")
        private String time;

        @JsonProperty("temperature_2m")
        private Double temperature2m;

        @JsonProperty("apparent_temperature")
        private Double apparentTemperature;

        @JsonProperty("pressure_msl")
        private Double surfacePressure;

        @JsonProperty("wind_speed_10m")
        private Double windSpeed10m;

        @JsonProperty("wind_gusts_10m")
        private Double windGusts10m;

        @JsonProperty("wind_direction_10m")
        private Integer windDirection10m;

        @JsonProperty("cloud_cover")
        private Integer cloudCover;

        @JsonProperty("rain")
        private Double rain;

        @JsonProperty("weather_code")
        private Integer weatherCode;
    }

    /**
     * Represents hourly weather data retrieved from the OpenMeteo API.
     * This class encapsulates various weather attributes across multiple hours,
     * providing detailed weather information in a structured format.
     *
     * Each attribute is represented as a list to accommodate weather data
     * for sequential hourly periods.
     *
     * Key attributes include:
     * - Time: A list of ISO 8601 formatted strings representing the timestamps for hourly data.
     * - Temperature at 2 meters: A list of temperatures in degrees Celsius for each hour.
     * - Apparent temperature: A list of perceived temperatures in degrees Celsius for each hour.
     * - Surface pressure: A list of atmospheric pressure values in hPa at the surface level for each hour.
     * - Rain: A list of accumulated rainfall amounts in millimeters for each hour.
     * - Cloud cover: A list of percentages indicating cloud coverage for each hour.
     * - Weather code: A list of integers representing weather condition codes for each hour.
     * - Wind speed at 10 meters: A list of wind speeds in m/s at a height of 10 meters for each hour.
     * - Wind gusts at 10 meters: A list of wind gust speeds in m/s at a height of 10 meters for each hour.
     * - Wind direction at 10 meters: A list of integers indicating wind direction in degrees for each hour.
     * - UV index: A list of UV index values for each hour.
     *
     * This class is designed to process and store hourly weather data as part of the
     * response from the OpenMeteo API, enabling detailed hourly forecasts or analytics.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HourlyDto {

        @JsonProperty("time")
        private List<String> time;

        @JsonProperty("temperature_2m")
        private List<Double> temperature2m;

        @JsonProperty("apparent_temperature")
        private List<Double> apparentTemperature;

        @JsonProperty("pressure_msl")
        private List<Double> surfacePressure;

        @JsonProperty("rain")
        private List<Double> rain;

        @JsonProperty("cloud_cover")
        private List<Integer> cloudCover;

        @JsonProperty("weather_code")
        private List<Integer> weatherCode;

        @JsonProperty("wind_speed_10m")
        private List<Double> windSpeed10m;

        @JsonProperty("wind_gusts_10m")
        private List<Double> windGusts10m;

        @JsonProperty("wind_direction_10m")
        private List<Integer> windDirection10m;

        @JsonProperty("uv_index")
        private List<Double> uvIndex;
    }

    /**
     * Represents daily weather data retrieved from the OpenMeteo API.
     * This class encapsulates various daily weather attributes such as
     * time, sunrise, sunset, and maximum UV index values for given days.
     *
     * Each attribute is represented as a list to accommodate daily weather
     * information for multiple days.
     *
     * Key attributes include:
     * - Time: List of ISO 8601 formatted date strings representing the days.
     * - Sunrise: List of ISO 8601 formatted time strings for sunrise times per day.
     * - Sunset: List of ISO 8601 formatted time strings for sunset times per day.
     * - UV Index Max: List of maximum UV index values per day.
     *
     * This data can be used for constructing daily weather summaries or forecasts,
     * where multi-day weather details are required.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DailyDto {
        @JsonProperty("time")
        private List<String> time;

        @JsonProperty("sunrise")
        private List<String> sunrise;

        @JsonProperty("sunset")
        private List<String> sunset;

        @JsonProperty("uv_index_max")
        private List<Double> uvIndexMax;
    }
}