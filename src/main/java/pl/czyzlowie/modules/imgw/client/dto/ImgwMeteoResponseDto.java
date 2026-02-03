package pl.czyzlowie.modules.imgw.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ImgwMeteoResponseDto {

    @JsonProperty("kod_stacji")
    private String stationId;

    @JsonProperty("nazwa_stacji")
    private String stationName;

    @JsonProperty("lat")
    private String latitude;

    @JsonProperty("lon")
    private String longitude;

    // --- TEMPERATURY ---
    @JsonProperty("temperatura_powietrza")
    private String airTemp;

    @JsonProperty("temperatura_powietrza_data")
    private String airTempTime;

    @JsonProperty("temperatura_gruntu")
    private String groundTemp;

    @JsonProperty("temperatura_gruntu_data")
    private String groundTempTime;

    // --- WIATR ---
    @JsonProperty("wiatr_kierunek")
    private String windDirection;

    @JsonProperty("wiatr_srednia_predkosc")
    private String windAvgSpeed;

    @JsonProperty("wiatr_predkosc_maksymalna")
    private String windMaxSpeed;

    @JsonProperty("wiatr_srednia_predkosc_data")
    private String windMeasurementTime;

    @JsonProperty("wiatr_poryw_10min")
    private String windGust10min;

    @JsonProperty("wiatr_poryw_10min_data")
    private String windGust10minTime;

    // --- INNE ---
    @JsonProperty("wilgotnosc_wzgledna")
    private String relativeHumidity;

    @JsonProperty("wilgotnosc_wzgledna_data")
    private String relativeHumidityTime;

    @JsonProperty("opad_10min")
    private String precipitation10min;

    @JsonProperty("opad_10min_data")
    private String precipitation10minTime;
}