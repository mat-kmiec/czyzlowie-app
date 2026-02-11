package pl.czyzlowie.modules.forecast.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Represents a simplified response from the OpenMeteo API.
 * It contains data related to the current weather conditions.
 *
 * This class serves as a lightweight version tailored to encapsulate
 * only the essential elements from the broader OpenMeteo response,
 * focusing exclusively on the current weather data.
 *
 * The structure of this response includes:
 * - A nested data class, `CurrentLightDto`, that holds the weather metrics for the current time.
 *
 * This is intended for scenarios where only basic weather information is required,
 * excluding more complex or extensive forecasts (hourly or daily).
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMeteoLightResponse {

    @JsonProperty("current")
    private CurrentLightDto current;

    /**
     * Represents the current weather data retrieved from the OpenMeteo API.
     * This class encapsulates various weather attributes such as temperature, pressure,
     * wind details, humidity, rainfall, etc., for the current time.
     *
     * Key attributes include:
     * - Time of the weather data retrieval.
     * - Current temperature, apparent temperature, and surface pressure.
     * - Wind speed, direction, and gusts at a height of 10 meters.
     * - Amount of rainfall.
     * - Relative humidity at a height of 2 meters.
     * - Weather code indicating the current weather condition class.
     *
     * It is designed to be used as part of the OpenMeteoLightResponse class to process
     * API responses. Attributes are mapped to JSON properties returned by the API.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurrentLightDto {

        @JsonProperty("time")
        private String time;

        @JsonProperty("temperature_2m")
        private Double temperature;

        @JsonProperty("surface_pressure")
        private Double pressure;

        @JsonProperty("wind_speed_10m")
        private Double windSpeed;

        @JsonProperty("wind_direction_10m")
        private Integer windDirection;

        @JsonProperty("wind_gusts_10m")
        private Double windGusts;

        @JsonProperty("rain")
        private Double rain;

        @JsonProperty("relative_humidity_2m")
        private Double humidity;

        @JsonProperty("weather_code")
        private Integer weatherCode;

        @JsonProperty("apparent_temperature")
        private Double apparentTemperature;
    }
}