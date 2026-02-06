package pl.czyzlowie.modules.forecast.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMeteoLightResponse {

    @JsonProperty("current")
    private CurrentLightDto current;

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

        // To opcjonalne, IMGW tego nie ma wprost, ale warto mieć odczuwalną
        @JsonProperty("apparent_temperature")
        private Double apparentTemperature;
    }
}