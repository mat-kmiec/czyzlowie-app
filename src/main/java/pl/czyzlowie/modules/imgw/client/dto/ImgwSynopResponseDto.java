package pl.czyzlowie.modules.imgw.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing synoptic response data from the
 * IMGW service. This class is designed to map synoptic weather data returned
 * by the service into a structured Java object for easier access and manipulation.
 *
 * The fields within this class represent key weather measurements, including
 * station metadata, measurement time, temperature, wind details, humidity,
 * precipitation, and atmospheric pressure.
 *
 * Each field is annotated with {@code @JsonProperty}, allowing JSON properties
 * from the service response to be correctly mapped to their corresponding Java
 * fields during data deserialization.
 *
 * The class leverages Lombok's {@code @Data} annotation to generate common methods
 * such as getters, setters, equals, hashCode, and toString, promoting cleaner
 * and more concise code.
 */
@Data
public class ImgwSynopResponseDto {

    /**
     * Represents a unique identifier for the synoptic weather station.
     */
    @JsonProperty("id_stacji")
    private String stationId;

    /**
     * Represents the name of the synoptic weather station as provided by
     * the IMGW service.
     */
    @JsonProperty("stacja")
    private String stationName;

    /**
     * Represents the date of the synoptic measurement as reported by the
     * IMGW service.
     */
    @JsonProperty("data_pomiaru")
    private String measurementDate;

    /**
     * Represents the hour at which the synoptic measurement was recorded.
     */
    @JsonProperty("godzina_pomiaru")
    private String measurementHour;

    /**
     * Represents the air temperature measurement as reported by the IMGW service.
     */
    @JsonProperty("temperatura")
    private String temperature;

    /**
     * Represents the wind speed measurement as reported by the IMGW service.
     */
    @JsonProperty("predkosc_wiatru")
    private String windSpeed;

    /**
     * Represents the direction of the wind as reported by the IMGW service.
     */
    @JsonProperty("kierunek_wiatru")
    private String windDirection;

    /**
     * Represents the relative humidity measurement as a percentage, typically
     * used to indicate the amount of moisture present in the air.
     */
    @JsonProperty("wilgotnosc_wzgledna")
    private String relativeHumidity;

    /**
     * Represents the total precipitation data for a specific measurement station.
     */
    @JsonProperty("suma_opadu")
    private String totalPrecipitation;

    /**
     * Represents the atmospheric pressure at a given measurement station.
     */
    @JsonProperty("cisnienie")
    private String pressure;
}