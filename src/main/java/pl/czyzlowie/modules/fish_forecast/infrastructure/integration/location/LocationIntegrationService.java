package pl.czyzlowie.modules.fish_forecast.infrastructure.integration.location;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.location.enums.StationCategory;
import pl.czyzlowie.modules.location.service.LocationFinderService;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class LocationIntegrationService {


    private final LocationFinderService locationFinderService;

    /**
     * Finds the nearest stations of various types (synoptic, meteorological, and optionally hydrological)
     * relative to the specified geographic coordinates.
     *
     * @param lat the latitude of the target location
     * @param lon the longitude of the target location
     * @param ignoreHydro a flag indicating whether to ignore searching for the nearest hydrological station
     * @return a {@code CompletableFuture} containing a {@code NearestStations} instance with the nearest stations
     */
    @Async("locationExecutor")
    public CompletableFuture<NearestStations> findNearestStations(Double lat, Double lon, boolean ignoreHydro){
            var synop = locationFinderService.findNearestStation(lat, lon, StationCategory.SYNOPTIC);
            var meteo = locationFinderService.findNearestStation(lat, lon, StationCategory.METEO);
            var hydro = ignoreHydro ? null : locationFinderService.findNearestStation(lat, lon, StationCategory.HYDRO);
            return CompletableFuture.completedFuture(new NearestStations(synop, meteo, hydro));
    }
}
