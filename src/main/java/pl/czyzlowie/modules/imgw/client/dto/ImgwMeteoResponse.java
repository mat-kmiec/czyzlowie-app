package pl.czyzlowie.modules.imgw.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImgwMeteoResponse {

    @JsonProperty("id_stacji")
    private String stationId;
    @JsonProperty("stacja")
    private String station;
    @JsonProperty("data_pomiaru")
    private String measurementDate;
    @JsonProperty("godzina_pomiaru")
    private String measurementHour;
    @JsonProperty("temperatura")
    private String temperature;
    @JsonProperty("predkosc_wiatru")
    private String windSpeed;
    @JsonProperty("kierunek_wiatru")
    private String windDirection;
    @JsonProperty("wilgotnosc_wzgledna")
    private String relativeHumidity;
    @JsonProperty("suma_opadu")
    private String totalPrecipitation;
    @JsonProperty("cisnienie")
    private String pressure;
}
