package pl.czyzlowie.modules.forecast.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.Optional;

/**
 * Client responsible for interacting with the Open-Meteo API to fetch weather data.
 * This class provides methods to retrieve and deserialize weather data responses
 * from the Open-Meteo API using HTTP requests.
 *
 * The client uses an instance of {@code RestClient} to perform HTTP GET requests.
 * If any errors occur during the request or deserialization process, the client
 * logs the error and provides an empty result.
 *
 * This component is designed to be used within a dependency injection framework
 * such as Spring, with logging enabled via SLF4J.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OpenMeteoClient {

    private final RestClient restClient;

    /**
     * Fetches data from a remote endpoint and attempts to deserialize it into the specified response type.
     * This method uses the provided URL to make an HTTP GET request, expecting a response of the type specified.
     * If an error occurs during the request or deserialization process, an empty {@code Optional} is returned.
     *
     * @param <T> the expected response type
     * @param url the URL of the endpoint from which the data is to be fetched
     * @param responseType the class of the expected response type, used for deserialization
     * @return an {@code Optional} containing the deserialized response if successful, or an empty {@code Optional} if an error occurs
     */
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