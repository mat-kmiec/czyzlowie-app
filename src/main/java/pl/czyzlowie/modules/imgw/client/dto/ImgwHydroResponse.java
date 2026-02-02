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
public class ImgwHydroResponse {
    @JsonProperty("id_stacji")
    private String stationId;
    @JsonProperty("stacja")
    private String station;
    @JsonProperty("rzeka")
    private String river;
    @JsonProperty("wojewodztwo")
    private String province;
    @JsonProperty("lon")
    private String longitude;
    @JsonProperty("lat")
    private String latitude;
    @JsonProperty("stan_wody")
    private String waterLevel;
    @JsonProperty("stan_wody_data_pomiaru")
    private String waterLevelDate;
    @JsonProperty("temperatura_wody")
    private String waterTemperature;
    @JsonProperty("temperatura_wody_data_pomiaru")
    private String waterTemperatureDate;
    @JsonProperty("przelyw")
    private String waterFlow;
    @JsonProperty("przeplyw_data_pomiaru")
    private String waterFlowDate;
    @JsonProperty("zjawisko_lodowe")
    private String icePhenomenon;
    @JsonProperty("zjawisko_lodowe_data_pomiaru")
    private String icePhenomenonDate;
    @JsonProperty("zjawisko_zarastania")
    private String overgrowthPhenomenon;
    @JsonProperty("zjawisko_zarastania_data_pomiaru")
    private String overgrowthPhenomenonDate;
}
