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