package pl.czyzlowie.modules.imgw.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * The ImgwClient class provides functionality to fetch data from an external
 * source using an HTTP client. It leverages a RestClient to perform HTTP
 * requests and process the responses.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ImgwClient {

    private final RestClient restClient;

    /**
     * Fetches a list of data from the given URL using the provided response type for deserialization.
     *
     * @param <T> the type of elements in the list
     * @param url the URL to fetch the data from
     * @param responseType the parameterized type reference for deserialization
     * @return the list of deserialized objects; if the data cannot be retrieved, returns an empty list
     */
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