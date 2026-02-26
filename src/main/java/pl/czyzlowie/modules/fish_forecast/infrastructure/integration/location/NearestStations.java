package pl.czyzlowie.modules.fish_forecast.infrastructure.integration.location;

import pl.czyzlowie.modules.location.service.LocationFinderService;

public record NearestStations(
        LocationFinderService.NearestStation synopStation,
        LocationFinderService.NearestStation meteo,
        LocationFinderService.NearestStation hydro
) {}
