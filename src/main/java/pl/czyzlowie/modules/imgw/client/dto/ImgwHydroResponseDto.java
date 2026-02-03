package pl.czyzlowie.modules.imgw.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ImgwHydroResponseDto {

    @JsonProperty("id_stacji")
    private String stationId;

    @JsonProperty("stacja")
    private String stationName;

    @JsonProperty("rzeka")
    private String river;

    @JsonProperty("wojewodztwo")
    private String province;

    @JsonProperty("lat")
    private String latitude;

    @JsonProperty("lon")
    private String longitude;

    // --- WODA ---
    @JsonProperty("stan_wody")
    private String waterLevel;

    @JsonProperty("stan_wody_data_pomiaru")
    private String waterLevelDate;

    @JsonProperty("temperatura_wody")
    private String waterTemperature;

    @JsonProperty("temperatura_wody_data_pomiaru")
    private String waterTemperatureDate;

    // --- PRZEPŁYW ---
    @JsonProperty("przelyw")
    private String discharge;

    @JsonProperty("przeplyw_data")
    private String dischargeDate;

    // --- ZJAWISKA ---
    @JsonProperty("zjawisko_lodowe")
    private String icePhenomenon;

    @JsonProperty("zjawisko_lodowe_data_pomiaru")
    private String icePhenomenonDate;

    @JsonProperty("zjawisko_zarastania")
    private String overgrowthPhenomenon;

    @JsonProperty("zjawisko_zarastania_data_pomiaru")
    private String overgrowthPhenomenonDate;
}