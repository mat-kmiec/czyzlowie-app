package pl.czyzlowie.modules.imgw_api.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.imgw_api.service.ImgwFetchFacade;

/**
 * A scheduler for triggering periodic fetching of meteorological, hydrological, and synoptic data.
 *
 * This class uses Spring's {@code @Scheduled} annotation to execute data-fetching tasks at fixed intervals,
 * as defined by external configuration properties. The data-fetching operations are delegated to the
 * {@code ImgwFetchFacade}, which handles the orchestrated fetching and processing of the respective data types.
 *
 * Each scheduled task logs its start, success, or failure, with detailed exception information when errors occur.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ImgwDataSceduler {

    private final ImgwFetchFacade fetchFacade;

    /**
     * Schedules the automatic fetching of meteorological data at a fixed rate.
     *
     * This method triggers the meteorological data fetch operation by invoking {@code fetchMeteo}
     * on the {@code fetchFacade}. It logs the start, success, or failure of the fetching process.
     * Any exceptions occurring during the operation are caught and logged with the error details.
     *
     * The execution interval is defined and configured externally using the property
     * {@code imgw.scheduler.meteo.html-rate}.
     */
    @Scheduled(fixedRateString = "${imgw.scheduler.meteo-rate}")
    public void scheduleMeteoFetch(){
        log.info("Auto-Fetching: METEO start");
        try{
            fetchFacade.fetchMeteo();
            log.info("Auto-Fetching: METEO success");
        } catch (Exception e){
            log.error("Auto-Fetching: METEO failed", e);
        }
    }

    /**
     * Schedules the automatic fetching of hydrological data at a fixed rate.
     *
     * This method triggers the hydrological data fetch operation by invoking {@code fetchHydro}
     * on the {@code fetchFacade}. It logs the start, success, or failure of the fetching process.
     * Any exceptions occurring during the operation are caught and logged with the error details.
     *
     * The execution interval is defined and configured externally using the property
     * {@code imgw.scheduler.hydro-rate}.
     */
    @Scheduled(fixedRateString = "${imgw.scheduler.hydro-rate}")
    public void scheduleHydroFetch(){
        log.info("Auto-Fetching: HYDRO start");
        try{
            fetchFacade.fetchHydro();
            log.info("Auto-Fetching: HYDRO success");
        } catch (Exception e){
            log.error("Auto-Fetching: HYDRO failed", e);
        }
    }

    /**
     * Schedules the automatic fetching of SYNOP (synoptic) data at a fixed rate.
     *
     * This method triggers the fetch operation by invoking {@code fetchSynop} on the
     * {@code fetchFacade}. It logs the start, success, or failure of the fetching process.
     * Any exceptions occurring during the operation are caught and logged with error details.
     *
     * The execution interval is defined and configured externally using the property
     * {@code imgw.scheduler.synop-rate}.
     */
    @Scheduled(fixedRateString = "${imgw.scheduler.synop-rate}")
    public void scheduleSynopFetch(){
        log.info("Auto-Fetching: SYNOP start");
        try{
            fetchFacade.fetchSynop();
            log.info("Auto-Fetching: SYNOP success");
        } catch (Exception e){
            log.error("Auto-Fetching: SYNOP failed", e);
        }
    }
}
