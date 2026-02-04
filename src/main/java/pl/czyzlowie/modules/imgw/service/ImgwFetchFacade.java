package pl.czyzlowie.modules.imgw.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * Facade class responsible for orchestrating the fetching and processing of
 * meteorological, hydrological, and synoptic data.
 *
 * This class consolidates operations for retrieving and processing various
 * data types by delegating to specific services for each type. It also
 * provides structured logging to improve traceability and facilitate debugging.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImgwFetchFacade {

    private final ImgwMeteoFetchService meteoService;
    private final ImgwHydroFetchService hydroService;
    private final ImgwSynopFetchService synopService;

    /**
     * Fetches and processes meteorological, hydrological, and synoptic data in sequence.
     *
     * This method serves as a facade for initiating the fetching and processing operations
     * for all data types handled by the service. It invokes the methods {@code fetchMeteo},
     * {@code fetchHydro}, and {@code fetchSynop} in order, ensuring that all data
     * retrieval processes are executed.
     *
     * Comprehensive logging is included to mark the beginning and end of the entire process
     * for easier traceability and debugging.
     */
    public void fetchAll() {
        log.info("--- START FETCH ALL ---");
        fetchMeteo();
        fetchHydro();
        fetchSynop();
        log.info("--- END FETCH ALL ---");
    }

    /**
     * Fetches and processes meteorological data using the {@code meteoService}.
     *
     * This method logs the start and end of the operation, and delegates the
     * actual fetching and processing logic to the {@code fetchAndProcess} method
     * of the {@code meteoService}.
     */
    public void fetchMeteo() {
        log.info("--- START FETCH METEO ---");
        meteoService.fetchAndProcess();
        log.info("--- END FETCH METEO ---");
    }

    /**
     * Fetches and processes hydro data by delegating the operation to the {@code hydroService}.
     * This method logs the start and end of the execution to provide traceability in logging outputs.
     */
    public void fetchHydro() {
        log.info("--- START FETCH HYDRO ---");
        hydroService.fetchAndProcess();
        log.info("--- END FETCH HYDRO ---");
    }

    /**
     * Fetches and processes SYNOP (synoptic) data using the {@code synopService}.
     * This method logs the start and end of the operation and delegates the
     * actual fetching and processing logic to the {@code fetchAndProcess}
     * method of {@code synopService}.
     */
    public void fetchSynop() {
        log.info("--- START FETCH SYNOP ---");
        synopService.fetchAndProcess();
        log.info("--- END FETCH SYNOP ---");
    }
}