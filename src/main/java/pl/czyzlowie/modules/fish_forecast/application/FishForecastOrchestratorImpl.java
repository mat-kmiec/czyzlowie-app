package pl.czyzlowie.modules.fish_forecast.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.barometer.entity.StationType;
import pl.czyzlowie.modules.fish_forecast.api.dto.FishForecastRequestDto;
import pl.czyzlowie.modules.fish_forecast.api.dto.FishForecastResponseDto;
import pl.czyzlowie.modules.fish_forecast.domain.engine.ForecastEngine;
import pl.czyzlowie.modules.fish_forecast.domain.engine.GlobalForecastResult;
import pl.czyzlowie.modules.fish_forecast.domain.model.*;
import pl.czyzlowie.modules.fish_forecast.infrastructure.integration.fish.FishProfileIntegrationService;
import pl.czyzlowie.modules.fish_forecast.infrastructure.integration.hydro.HydroIntegrationService;
import pl.czyzlowie.modules.fish_forecast.infrastructure.integration.location.LocationIntegrationService;
import pl.czyzlowie.modules.fish_forecast.infrastructure.integration.location.NearestStations;
import pl.czyzlowie.modules.fish_forecast.infrastructure.integration.meteo.MeteoIntegrationService;
import pl.czyzlowie.modules.fish_forecast.infrastructure.integration.moon.MoonIntegrationService;
import pl.czyzlowie.modules.fish_forecast.infrastructure.integration.synop.SynopIntegrationService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of the FishForecastOrchestrator interface responsible for orchestrating
 * the fish forecast generation process. This class integrates data from multiple services
 * and combines them to create a fishing forecast using the ForecastEngine.
 *
 * This service utilizes various integration services to fetch data about
 * geographical locations, weather patterns, hydrological data, moon phases,
 * synoptical data, and fish profiles, and uses the gathered data to create
 * predictions.
 *
 * Key responsibilities:
 * - Fetching and orchestrating data from multiple integration services.
 * - Building the WeatherContext using data from relevant stations and integration services.
 * - Combining the built context with target fish profiles to calculate fishing forecasts.
 * - Leveraging the ForecastEngine to compute the result.
 *
 * Dependencies:
 * - LocationIntegrationService: Responsible for locating the nearest measurement stations
 *   for hydro, meteo, synop, and moon data.
 * - HydroIntegrationService: Fetches hydrological data.
 * - MeteoIntegrationService: Fetches meteorological data.
 * - MoonIntegrationService: Provides moon phase data.
 * - SynopIntegrationService: Supplies synoptical weather data.
 * - FishProfileIntegrationService: Retrieves profiles for targeted fish species.
 * - ForecastEngine: Performs the forecast calculations based on the gathered data.
 *
 * Logging:
 * - Logs the start and completion status of the forecast generation processes.
 * - Logs intermediate steps such as WeatherContext building and forecast calculations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FishForecastOrchestratorImpl implements FishForecastOrchestrator {

    private final LocationIntegrationService locationIntegrationService;
    private final HydroIntegrationService hydroIntegrationService;
    private final MeteoIntegrationService meteoIntegrationService;
    private final MoonIntegrationService moonIntegrationService;
    private final SynopIntegrationService synopIntegrationService;
    private final FishProfileIntegrationService fishProfileIntegrationService;
    private final ForecastEngine forecastEngine;

    /**
     * Calculates a fish forecast based on the given request data. The method integrates
     * various external services to gather weather, hydrological, and other environmental
     * data, processes it, and computes a fishing forecast using a forecasting engine.
     *
     * @param req the request data containing information such as latitude, longitude, target
     *            time, and specific fish species identifiers for the forecast.
     * @return a CompletableFuture containing the result of the fish forecast calculation
     * including details such as general bite index and other forecast-related data.
     */
    @Override
    public CompletableFuture<FishForecastResponseDto> calculateFishForecast(FishForecastRequestDto req) {

        log.info("Rozpoczęto generowanie prognozy NASA dla lat: {}, lon: {}, cel czasowy: {}", req.lat(), req.lon(), req.targetTime());

        CompletableFuture<List<FishProfile>> profilesFuture = fishProfileIntegrationService
                .fetchTargetProfiles(req.targetFishSpeciesIds());

        CompletableFuture<NearestStations> stationsFuture = locationIntegrationService
                .findNearestStations(req.lat(), req.lon(), req.ignoreHydro());

        CompletableFuture<WeatherContext> weatherContextFuture = stationsFuture.thenCompose(stations -> {

            Long hydroId = (stations.hydro() != null) ? Long.valueOf(stations.hydro().stationId()) : null;
            Long meteoId = (stations.meteo() != null) ? Long.valueOf(stations.meteo().stationId()) : null;
            String synopStationIdStr = null;
            boolean isSynopVirtual = false;
            String moonStationId = null;

            if (stations.synopStation() != null) {
                isSynopVirtual = (stations.synopStation().type() == StationType.VIRTUAL);
                synopStationIdStr = stations.synopStation().stationId();
                moonStationId = stations.synopStation().stationId();
            }

            CompletableFuture<List<HydroSnapshot>> hydroF = req.ignoreHydro()
                    ? CompletableFuture.completedFuture(List.of())
                    : hydroIntegrationService.fetchHydroTimeline(hydroId, req.targetTime());

            CompletableFuture<List<MeteoSnapshot>> meteoF = req.ignoreMeteo()
                    ? CompletableFuture.completedFuture(List.of())
                    : meteoIntegrationService.fetchMeteoTimeline(meteoId, req.targetTime());

            String moonStationType = isSynopVirtual ? "VIRTUAL" : "SYNOP";

            CompletableFuture<List<MoonSnapshot>> moonF = moonIntegrationService
                    .fetchMoonTimeline(moonStationId, moonStationType, req.targetTime());

            CompletableFuture<List<SynopSnapshot>> synopF = synopIntegrationService
                    .fetchSynopTimeline(synopStationIdStr, isSynopVirtual, req.targetTime());

            return CompletableFuture.allOf(hydroF, meteoF, moonF, synopF)
                    .thenApply(v -> WeatherContext.builder()
                            .hydroTimeline(hydroF.join())
                            .meteoTimeline(meteoF.join())
                            .moonTimeline(moonF.join())
                            .synopTimeline(synopF.join())
                            .build()
                    );
        });

        return weatherContextFuture.thenCombine(profilesFuture, (context, profiles) -> {
            log.info("Zbudowano WeatherContext. Rozpoczynam kalkulacje w ForecastEngine...");
            GlobalForecastResult result = forecastEngine.calculate(context, profiles, req.targetTime().toLocalDateTime()).join();
            log.info("Zakończono kalkulacje! Wynik globalny (Bite Index): {}%", result.generalBiteIndex());

            return new FishForecastResponseDto("SUCCESS", "Prognoza wędkarska została wygenerowana pomyślnie!", result);
        });
    }
}