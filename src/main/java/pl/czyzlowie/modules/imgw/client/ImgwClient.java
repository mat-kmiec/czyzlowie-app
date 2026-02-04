package pl.czyzlowie.modules.imgw.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImgwClient {

    private final RestClient restClient = RestClient.create();

    public <T> List<T> fetchList(String url, ParameterizedTypeReference<List<T>> responseType) {
        try {
            List<T> result = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(responseType);
            return result != null ? result : List.of();
        } catch (Exception e) {
            log.error("Błąd pobierania danych z URL: {}", url, e);
            return List.of();
        }
    }
}


https://api.open-meteo.com/v1/forecast?
// latitude=52.22&longitude=21.01&
// current=temperature_2m,apparent_temperature,surface_pressure,wind_speed_10m,wind_gusts_10m,wind_direction_10m,cloud_cover,rain,weather_code&hourly=temperature_2m,apparent_temperature,surface_pressure,rain,weather_code,wind_speed_10m,wind_gusts_10m,wind_direction_10m,uv_index&daily=sunrise,sunset,uv_index_max&forecast_days=4&timezone=Europe%2FWarsaw