package pl.czyzlowie.modules.fish_forecast.infrastructure.integration.location;

import pl.czyzlowie.modules.location.service.LocationFinderService;

/**
 * Represents a collection of the nearest stations of various types relative to a specified geographic location.
 * This record encapsulates the nearest synoptic, meteorological, and hydrological stations.
 *
 * The synoptic, meteorological, and hydrological stations are instances of the
 * {@link LocationFinderService.NearestStation} record, which include information about
 * the station's identifier, type, and its distance in kilometers from the specified location.
 *
 * This class is primarily used to aggregate the nearest station data for different station categories
 * as determined by the {@link LocationFinderService}.
 *
 * @param synopStation the nearest synoptic station
 * @param meteo the nearest meteorological station
 * @param hydro the nearest hydrological station
 */
public record NearestStations(
        LocationFinderService.NearestStation synopStation,
        LocationFinderService.NearestStation meteo,
        LocationFinderService.NearestStation hydro
) {}
