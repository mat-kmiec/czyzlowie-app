package pl.czyzlowie.modules.fish_forecast.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.barometer.entity.StationType;
import pl.czyzlowie.modules.fish_forecast.api.dto.FishForecastRequestDto;
import pl.czyzlowie.modules.fish_forecast.api.dto.FishForecastResponseDto;
import pl.czyzlowie.modules.fish_forecast.domain.model.*;
import pl.czyzlowie.modules.fish_forecast.infrastructure.integration.hydro.HydroIntegrationService;
import pl.czyzlowie.modules.fish_forecast.infrastructure.integration.location.LocationIntegrationService;
import pl.czyzlowie.modules.fish_forecast.infrastructure.integration.location.NearestStations;
import pl.czyzlowie.modules.fish_forecast.infrastructure.integration.meteo.MeteoIntegrationService;
import pl.czyzlowie.modules.fish_forecast.infrastructure.integration.moon.MoonIntegrationService;
import pl.czyzlowie.modules.fish_forecast.infrastructure.integration.synop.SynopIntegrationService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class FishForecastOrchestratorImpl implements FishForecastOrchestrator {

    private final LocationIntegrationService locationIntegrationService;
    private final HydroIntegrationService hydroIntegrationService;
    private final MeteoIntegrationService meteoIntegrationService;
    private final MoonIntegrationService moonIntegrationService;
    private final SynopIntegrationService synopIntegrationService;

    @Override
    public CompletableFuture<FishForecastResponseDto> calculateFishForecast(FishForecastRequestDto req) {

        CompletableFuture<NearestStations> stationsFuture = locationIntegrationService
                .findNearestStations(req.lat(), req.lon(), req.ignoreHydro());

        CompletableFuture<WeatherContext> weatherContextFuture = stationsFuture.thenCompose(stations -> {

            Long hydroId = (stations.hydro() != null) ? Long.valueOf(stations.hydro().stationId()) : null;
            Long meteoId = (stations.meteo() != null) ? Long.valueOf(stations.meteo().stationId()) : null;

            Long synopId = null;
            boolean isSynopVirtual = false;
            String moonStationId = null;

            if (stations.synopStation() != null) {
                synopId = Long.valueOf(stations.synopStation().stationId());
                isSynopVirtual = (stations.synopStation().type() == StationType.VIRTUAL);
                moonStationId = stations.synopStation().stationId();
            }

            CompletableFuture<List<HydroSnapshot>> hydroF = req.ignoreHydro()
                    ? CompletableFuture.completedFuture(List.of())
                    : hydroIntegrationService.fetchHydroTimeline(hydroId, req.targetTime());

            CompletableFuture<List<MeteoSnapshot>> meteoF = req.ignoreMeteo()
                    ? CompletableFuture.completedFuture(List.of())
                    : meteoIntegrationService.fetchMeteoTimeline(meteoId, req.targetTime());

            CompletableFuture<List<MoonSnapshot>> moonF = moonIntegrationService
                    .fetchMoonTimeline(moonStationId, "SYNOPTIC", req.targetTime());

            CompletableFuture<List<SynopSnapshot>> synopF = synopIntegrationService
                    .fetchSynopTimeline(synopId, isSynopVirtual, req.targetTime());

            return CompletableFuture.allOf(hydroF, meteoF, moonF, synopF)
                    .thenApply(v -> WeatherContext.builder()
                            .hydroTimeline(hydroF.join())
                            .meteoTimeline(meteoF.join())
                            .moonTimeline(moonF.join())
                            .synopTimeline(synopF.join())
                            .build()
                    );
        });

        return weatherContextFuture.thenApply(context -> {
            System.out.println("\n=======================================================");
            System.out.println("             ZBUDOWANO PEŁNY KONTEKST POGODOWY           ");
            System.out.println("=======================================================\n");

            System.out.println("--- PRZYGOTOWANE DANE HYDRO (Chronologicznie -, +) ---");
            if (context.hydroTimeline().isEmpty()) {
                System.out.println("Brak danych hydro (lub zignorowano).");
            } else {
                context.hydroTimeline().forEach(h ->
                        System.out.printf("Czas: %s | Stan wody: %s cm | Temp wody: %s °C | Przepływ: %s m3/s | Lód (kod): %s | Zarastanie (kod): %s\n",
                                h.timestamp(),
                                h.waterLevel(),
                                h.waterTemperature(),
                                h.discharge(),
                                h.icePhenomenon(),
                                h.overgrowthPhenomenon())
                );
            }

            System.out.println("\n--- PRZYGOTOWANE DANE METEO (Chronologicznie -, +) ---");
            if (context.meteoTimeline().isEmpty()) {
                System.out.println("Brak danych meteo (lub zignorowano).");
            } else {
                context.meteoTimeline().forEach(m ->
                        System.out.printf("Czas: %s | Temp pow: %s °C | Temp gruntu: %s °C | Kierunek wiatru: %s° | Wiatr śr: %s | Wiatr max: %s | Poryw: %s | Wilgotność: %s%% | Opad (10m): %s mm\n",
                                m.timestamp(),
                                m.airTemperature(),
                                m.groundTemperature(),
                                m.windDirection(),
                                m.windAverageSpeed(),
                                m.windMaxSpeed(),
                                m.windGust(),
                                m.humidity(),
                                m.precipitation10min())
                );
            }

            System.out.println("\n--- PRZYGOTOWANE DANE KSIĘŻYCOWE I SŁONECZNE (Chronologicznie -, +) ---");
            if (context.moonTimeline().isEmpty()) {
                System.out.println("Brak danych księżycowych.");
            } else {
                context.moonTimeline().forEach(moon ->
                        System.out.printf("Data: %s | Faza: %s | Oświetlenie: %s%% | Wiek księżyca: %s dni | Superksiężyc: %s | Wschód Ks: %s | Zachód Ks: %s | Tranzyt Ks: %s | Wschód Sł: %s | Zachód Sł: %s\n",
                                moon.date(),
                                moon.phaseName(),
                                moon.illuminationPct(),
                                moon.moonAgeDays(),
                                moon.isSuperMoon(),
                                moon.moonrise(),
                                moon.moonset(),
                                moon.transit(),
                                moon.sunrise(),
                                moon.sunset())
                );
            }

            System.out.println("\n--- PRZYGOTOWANE DANE SYNOP (Historia + Prognoza) (Chronologicznie -, +) ---");
            if (context.synopTimeline().isEmpty()) {
                System.out.println("Brak danych synoptycznych.");
            } else {
                context.synopTimeline().forEach(s ->
                        System.out.printf("Czas: %s | Temp: %s °C | Temp odczuwalna: %s °C | Ciśnienie: %s hPa | Wiatr: %s km/h | Porywy: %s km/h | Kierunek: %s° | Wilgotność: %s%% | Opad: %s mm | Chmury: %s%% | UV: %s\n",
                                s.timestamp(),
                                s.temperature(),
                                s.apparentTemperature(),
                                s.pressure(),
                                s.windSpeed(),
                                s.windGusts(),
                                s.windDirection(),
                                s.humidity(),
                                s.precipitation(),
                                s.cloudCover(),
                                s.uvIndex())
                );
            }
            System.out.println("\n=======================================================\n");

            return new FishForecastResponseDto("SUCCESS", "Dane wypisane w konsoli!");
        });
    }
}
