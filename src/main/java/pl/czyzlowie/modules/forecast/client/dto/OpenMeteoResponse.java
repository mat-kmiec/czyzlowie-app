package pl.czyzlowie.modules.forecast.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

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


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurrentDto {
        @JsonProperty("time")
        private String time;

        @JsonProperty("temperature_2m")
        private Double temperature2m;

        @JsonProperty("apparent_temperature")
        private Double apparentTemperature;

        @JsonProperty("surface_pressure")
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

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HourlyDto {

        @JsonProperty("time")
        private List<String> time;

        @JsonProperty("temperature_2m")
        private List<Double> temperature2m;

        @JsonProperty("apparent_temperature")
        private List<Double> apparentTemperature;

        @JsonProperty("surface_pressure")
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