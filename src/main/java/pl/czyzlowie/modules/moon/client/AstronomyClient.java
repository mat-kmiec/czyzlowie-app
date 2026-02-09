package pl.czyzlowie.modules.moon.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient; // <--- Zmiana importu
import org.springframework.web.util.UriComponentsBuilder;
import pl.czyzlowie.modules.moon.client.dto.AstronomyResponse;

import java.time.LocalDate;
import java.util.Base64;

@Component
@RequiredArgsConstructor
@Slf4j
public class AstronomyClient {

    private final RestClient restClient;

    @Value("${astronomy.api.url}")
    private String apiUrl;

    @Value("${astronomy.api.app-id}")
    private String appId;

    @Value("${astronomy.api.app-secret}")
    private String appSecret;

    public AstronomyResponse fetchMoonData(double lat, double lon, LocalDate date) {
        String url = UriComponentsBuilder.fromUriString(apiUrl)
                .path("/bodies/positions")
                .queryParam("latitude", lat)
                .queryParam("longitude", lon)
                .queryParam("from_date", date.toString())
                .queryParam("to_date", date.toString())
                .queryParam("elevation", 100)
                .queryParam("time", "12:00:00")
                .build()
                .toUriString();

        String auth = appId + ":" + appSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        try {
            return restClient.get()
                    .uri(url)
                    .header("Authorization", "Basic " + encodedAuth)
                    .retrieve()
                    .body(AstronomyResponse.class);
        } catch (Exception e) {
            log.error("API Error [Astronomy]: {}", e.getMessage());
            return null;
        }
    }
}