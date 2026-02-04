package pl.czyzlowie.modules.forecast.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenMeteoClient {

    private final RestClient restClient = RestClient.create();

    public <T> Optional<T> fetchData(String url, Class<T> responseType) {
        try {
            T result = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(responseType);
            return Optional.ofNullable(result);
        } catch (Exception e) {
            log.error("Błąd pobierania prognozy z Open-Meteo URL: {}", url, e);
            return Optional.empty();
        }
    }
}