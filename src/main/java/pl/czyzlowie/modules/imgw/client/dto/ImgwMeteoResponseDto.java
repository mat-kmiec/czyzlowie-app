package pl.czyzlowie.modules.imgw.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing meteorological response data from the
 * IMGW service. This class maps the meteorological data returned by the service
 * to a Java object, providing easy access and manipulation of weather-related
 * information.
 *
 * The fields in this class correspond to various meteorological parameters,
 * such as station metadata, temperature, wind details, humidity, and
 * precipitation measurements. These parameters are essential for retrieving
 * and processing weather data.
 *
 * Each field is annotated with {@code @JsonProperty}, mapping JSON properties
 * from the response to the corresponding Java fields.
 *
 * The class leverages Lombok's {@code @Data} annotation to automatically
 * generate methods like getters, setters, equals, hashCode, and toString.
 */
@Data
public class ImgwMeteoResponseDto {

    /**
     * Represents a unique identifier for a meteorological station.
     */
    @JsonProperty("kod_stacji")
    private String stationId;

    /**
     * Represents the name of a meteorological station.
     */
    @JsonProperty("nazwa_stacji")
    private String stationName;

    /**
     * Represents the latitude coordinate of a meteorological station.
     */
    @JsonProperty("lat")
    private String latitude;

    /**
     * Represents the longitude coordinate of a geographical location.
     */
    @JsonProperty("lon")
    private String longitude;

    /**
     * Represents the air temperature measurement recorded by the IMGW weather
     * station.
     */
    // --- TEMPERATURY ---
    @JsonProperty("temperatura_powietrza")
    private String airTemp;

    /**
     * Represents the timestamp for the air temperature measurement.
     */
    @JsonProperty("temperatura_powietrza_data")
    private String airTempTime;

    /**
     * Represents the ground temperature data.
     */
    @JsonProperty("temperatura_gruntu")
    private String groundTemp;

    /**
     * Represents the timestamp of the ground temperature measurement.
     */
    @JsonProperty("temperatura_gruntu_data")
    private String groundTempTime;

    /**
     * Represents the direction of the wind as a textual description or code
     * (e.g., compass direction such as "N", "NE", etc.).
     */
    @JsonProperty("wiatr_kierunek")
    private String windDirection;

    /**
     * Represents the average wind speed measurement from the IMGW service.
     */
    @JsonProperty("wiatr_srednia_predkosc")
    private String windAvgSpeed;

    /**
     * Represents the maximum wind speed recorded at a meteorological station.
     */
    @JsonProperty("wiatr_predkosc_maksymalna")
    private String windMaxSpeed;

    @JsonProperty("wiatr_srednia_predkosc_data")
    private String windMeasurementTime;

    /**
     * Represents the maximum wind gust recorded over the last 10 minutes.
     */
    @JsonProperty("wiatr_poryw_10min")
    private String windGust10min;

    /**
     * Represents the timestamp of the most recent maximum wind gust measured
     * within a 10-minute period.
     */
    @JsonProperty("wiatr_poryw_10min_data")
    private String windGust10minTime;

    /**
     * Represents the relative humidity measurement provided by the IMGW service.
     */
    @JsonProperty("wilgotnosc_wzgledna")
    private String relativeHumidity;

    /**
     * Represents the timestamp at which the relative humidity measurement was
     * recorded.
     */
    @JsonProperty("wilgotnosc_wzgledna_data")
    private String relativeHumidityTime;

    /**
     * Represents the total precipitation measured during the last 10 minutes at a specific
     * weather station.
     */
    @JsonProperty("opad_10min")
    private String precipitation10min;

    /**
     * Represents the timestamp of the precipitation measurement in a 10-minute
     * interval.
     */
    @JsonProperty("opad_10min_data")
    private String precipitation10minTime;
}