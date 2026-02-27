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

    @Async("locationExecutor")
    public CompletableFuture<NearestStations> findNearestStations(Double lat, Double lon, boolean ignoreHydro){
            var synop = locationFinderService.findNearestStation(lat, lon, StationCategory.SYNOPTIC);
            var meteo = locationFinderService.findNearestStation(lat, lon, StationCategory.METEO);
            var hydro = ignoreHydro ? null : locationFinderService.findNearestStation(lat, lon, StationCategory.HYDRO);
            return CompletableFuture.completedFuture(new NearestStations(synop, meteo, hydro));
    }
}
