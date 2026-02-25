package pl.czyzlowie.modules.imgw_api.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing hydrological response data from the
 * IMGW service. This class maps the hydrological data returned by the service
 * to a Java object, facilitating easier manipulation and access to the data.
 * The fields in this class correspond to specific attributes of a hydrological
 * station and its measurements, including station metadata, water level,
 * temperature, discharge, and observed phenomena.
 *
 * Fields are mapped using the Jackson annotation {@code @JsonProperty} to
 * bind JSON properties from the response to the corresponding fields.
 *
 * This class uses Lombok's {@code @Data} annotation to automatically generate
 * common methods like getters, setters, equals, hashCode, and toString.
 */
@Data
public class ImgwHydroResponseDto {

    /**
     * Represents a unique identifier for the hydrological station.
     */
    @JsonProperty("id_stacji")
    private String stationId;

    /**
     * Represents the name of the hydrological station.
     */
    @JsonProperty("stacja")
    private String stationName;

    /**
     * Represents the name of the river associated with the hydrological data.
     */
    @JsonProperty("rzeka")
    private String river;

    /**
     * Represents the province in which the hydrological station is located.
     */
    @JsonProperty("wojewodztwo")
    private String province;

    /**
     * Represents the latitude coordinate of a geographical location.
     */
    @JsonProperty("lat")
    private String latitude;

    /**
     * Represents the longitude information of a station in the IMGW hydro response data.
     */
    @JsonProperty("lon")
    private String longitude;

    /**
     * Represents the water level measurement at a specific hydrological station.
     */
    @JsonProperty("stan_wody")
    private String waterLevel;

    /**
     * Represents the date and time of the water level measurement obtained
     */
    @JsonProperty("stan_wody_data_pomiaru")
    private String waterLevelDate;

    /**
     * Represents the water temperature data for a specific measurement
     * station provided by the IMGW service.
     */
    @JsonProperty("temperatura_wody")
    private String waterTemperature;

    /**
     * Represents the date and time of the water temperature measurement.
     */
    @JsonProperty("temperatura_wody_data_pomiaru")
    private String waterTemperatureDate;

    /**
     * Represents the current value of water discharge measured at a specific
     * hydrological station.
     */
    @JsonProperty("przelyw")
    private String discharge;

    /**
     * Represents the timestamp of the water discharge measurement in the
     * IMGW Hydrological data response.
     */
    @JsonProperty("przeplyw_data")
    private String dischargeDate;

    /**
     * Represents information about ice-related phenomena observed at a specific
     * hydrological measurement station.
     */
    @JsonProperty("zjawisko_lodowe")
    private String icePhenomenon;

    /**
     * Represents the date and time of the ice phenomenon measurement.
     */
    @JsonProperty("zjawisko_lodowe_data_pomiaru")
    private String icePhenomenonDate;

    /**
     * Represents a phenomenon related to vegetation or biological overgrowth, as reported
     * in hydrological data from the IMGW service.
     */
    @JsonProperty("zjawisko_zarastania")
    private String overgrowthPhenomenon;

    /**
     * Represents the measurement date of the overgrowth phenomenon, as provided
     * in the response from the IMGW service.
     */
    @JsonProperty("zjawisko_zarastania_data_pomiaru")
    private String overgrowthPhenomenonDate;
}