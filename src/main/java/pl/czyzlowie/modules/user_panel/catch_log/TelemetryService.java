package pl.czyzlowie.modules.user_panel.catch_log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.barometer.entity.StationType;
import pl.czyzlowie.modules.forecast.entity.VirtualStationData;
import pl.czyzlowie.modules.forecast.repository.VirtualStationDataRepository;
import pl.czyzlowie.modules.imgw_api.entity.ImgwHydroData;
import pl.czyzlowie.modules.imgw_api.entity.ImgwMeteoData;
import pl.czyzlowie.modules.imgw_api.entity.ImgwSynopData;
import pl.czyzlowie.modules.imgw_api.repository.ImgwHydroDataRepository;
import pl.czyzlowie.modules.imgw_api.repository.ImgwMeteoDataRepository;
import pl.czyzlowie.modules.imgw_api.repository.ImgwSynopDataRepository;
import pl.czyzlowie.modules.location.enums.StationCategory;
import pl.czyzlowie.modules.location.service.LocationFinderService;
import pl.czyzlowie.modules.location.service.LocationFinderService.NearestStation;
import pl.czyzlowie.modules.moon.repository.MoonGlobalDataRepository;
import pl.czyzlowie.modules.user_panel.catch_log.CatchRecord;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Service for enriching catch records with telemetry data such as meteorological, hydrological,
 * and lunar phase information obtained from various data sources and repositories.
 *
 * This service incorporates data from multiple sources including synoptic, virtual, and meteorological
 * stations, as well as lunar and hydrological repositories to augment catch records with relevant
 * environmental and atmospheric information.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryService {

    private final LocationFinderService locationFinderService;
    private final ImgwSynopDataRepository synopDataRepo;
    private final VirtualStationDataRepository virtualDataRepo;
    private final ImgwMeteoDataRepository meteoDataRepo;
    private final ImgwHydroDataRepository hydroDataRepo;
    private final MoonGlobalDataRepository moonDataRepo;

    /**
     * Enriches a {@code CatchRecord} object with meteorological data and moon phase information.
     * Retrieves the nearest meteorological and synoptic stations based on the latitude and longitude
     * in the record, fetches relevant data from repositories, and updates the record with the data.
     *
     * @param record The {@code CatchRecord} object containing catch details such as location
     *               (latitude and longitude) and catch date, which will be enriched with
     *               additional meteorological and moon-related data.
     */
    public void enrichWithMeteoAndMoon(CatchRecord record) {
        if (record.getLatitude() == null || record.getLongitude() == null) return;

        double lat = record.getLatitude().doubleValue();
        double lng = record.getLongitude().doubleValue();
        LocalDateTime catchTime = record.getCatchDate();

        moonDataRepo.findById(catchTime.toLocalDate())
                .ifPresent(moon -> record.setMoonPhase(moon.getPhaseMoonPl()));

        try {
            NearestStation synopStation = locationFinderService.findNearestStation(lat, lng, StationCategory.SYNOPTIC);
            if (synopStation.type() == StationType.IMGW_SYNOP) {
                synopDataRepo.findClosestSynopData(synopStation.stationId(), catchTime.toLocalDate(), catchTime.getHour())
                        .ifPresent(data -> applySynopData(record, data));
            } else if (synopStation.type() == StationType.VIRTUAL) {
                virtualDataRepo.findFirstByVirtualStationIdAndMeasurementTimeLessThanEqualOrderByMeasurementTimeDesc(Long.valueOf(synopStation.stationId()), catchTime)
                        .ifPresent(data -> applyVirtualData(record, data));
            }
        } catch (Exception e) {
            log.warn("Brak stacji synoptycznej dla rekordu", e);
        }

        try {
            NearestStation meteoStation = locationFinderService.findNearestStation(lat, lng, StationCategory.METEO);
            meteoDataRepo.findFirstByStationIdAndAirTempTimeLessThanEqualOrderByAirTempTimeDesc(meteoStation.stationId(), catchTime)
                    .ifPresent(data -> applyMeteoData(record, data));
        } catch (Exception e) {
            log.warn("Brak stacji meteo dla rekordu", e);
        }
    }

    /**
     * Enriches the given {@code CatchRecord} instance with hydro data such as water level, water temperature,
     * and discharge by finding the nearest hydro station and retrieving relevant historical data.
     * The operation is skipped if the record indicates to ignore hydro data or if its latitude or longitude is null.
     *
     * @param record the {@code CatchRecord} instance to be enriched with hydro data
     */
    public void enrichWithHydro(CatchRecord record) {
        if (record.isIgnoreHydro() || record.getLatitude() == null || record.getLongitude() == null) return;

        double lat = record.getLatitude().doubleValue();
        double lng = record.getLongitude().doubleValue();

        try {
            NearestStation hydroStation = locationFinderService.findNearestStation(lat, lng, StationCategory.HYDRO);
            hydroDataRepo.findFirstByStationIdAndWaterLevelDateLessThanEqualOrderByWaterLevelDateDesc(hydroStation.stationId(), record.getCatchDate())
                    .ifPresent(data -> {
                        record.setWaterLevel(data.getWaterLevel());
                        record.setWaterTemperature(data.getWaterTemperature());
                        record.setDischarge(data.getDischarge());
                    });
        } catch (Exception e) {
            log.warn("Brak stacji hydro dla rekordu", e);
        }
    }

    /**
     * Updates the {@code CatchRecord} with meteorological data from {@code ImgwSynopData}.
     * If any field in the {@code CatchRecord} is null, this method sets it with the corresponding value from {@code ImgwSynopData}.
     *
     * @param r The {@code CatchRecord} instance to be updated with synoptic data.
     * @param d The {@code ImgwSynopData} instance containing meteorological information such as temperature, pressure, humidity, and wind data.
     */
    private void applySynopData(CatchRecord r, ImgwSynopData d) {
        if (r.getAirTemperature() == null) r.setAirTemperature(d.getTemperature());
        if (r.getPressure() == null) r.setPressure(d.getPressure());
        if (r.getHumidity() == null) r.setHumidity(d.getRelativeHumidity());
        if (r.getWindSpeed() == null && d.getWindSpeed() != null) r.setWindSpeed(BigDecimal.valueOf(d.getWindSpeed()));
        if (r.getPrecipitation() == null) r.setPrecipitation(d.getTotalPrecipitation());
        if (r.getWindDirection() == null && d.getWindDirection() != null) r.setWindDirection(String.valueOf(d.getWindDirection()));
    }

    /**
     * Applies virtual station data to the given CatchRecord if certain fields in the record are null.
     *
     * @param r the CatchRecord object whose fields are to be updated with virtual station data
     * @param d the VirtualStationData object containing the virtual data used to update the CatchRecord
     */
    private void applyVirtualData(CatchRecord r, VirtualStationData d) {
        if (r.getAirTemperature() == null) r.setAirTemperature(d.getTemperature());
        if (r.getPressure() == null) r.setPressure(d.getPressure());
        if (r.getHumidity() == null) r.setHumidity(d.getHumidity());
        if (r.getWindSpeed() == null) r.setWindSpeed(d.getWindSpeed());
        if (r.getPrecipitation() == null) r.setPrecipitation(d.getRain());
        if (r.getWindDirection() == null && d.getWindDirection() != null) r.setWindDirection(String.valueOf(d.getWindDirection()));
    }

    /**
     * Updates the meteorological data fields of the provided CatchRecord object
     * using the data available in the ImgwMeteoData object, if those fields are
     * currently null in the CatchRecord object.
     *
     * @param r the CatchRecord object that requires meteorological data updates
     * @param d the ImgwMeteoData object containing the meteorological data to apply
     */
    private void applyMeteoData(CatchRecord r, ImgwMeteoData d) {
        if (r.getAirTemperature() == null) r.setAirTemperature(d.getAirTemp());
        if (r.getHumidity() == null) r.setHumidity(d.getRelativeHumidity());
        if (r.getPrecipitation() == null) r.setPrecipitation(d.getPrecipitation10min());
        if (r.getWindSpeed() == null) r.setWindSpeed(d.getWindAvgSpeed());
        if (r.getWindDirection() == null && d.getWindDirection() != null) r.setWindDirection(String.valueOf(d.getWindDirection()));
    }
}