package pl.czyzlowie.modules.imgw_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.imgw_api.entity.enums.ImgwImportType;


/**
 * Facade class responsible for orchestrating the fetching and processing of
 * meteorological, hydrological, and synoptic data.
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
    private final ImgwImportLogService imgwImportLogService;

    /**
     * Fetches and processes meteorological, hydrological, and synoptic data sequentially.
     * This method acts as a unified entry point for orchestrating the fetching
     * and processing multiple types of environmental data. It sequentially
     * triggers the following operations:
     * <ul>
     * - Fetching and processing meteorological data via {@code fetchMeteo}
     * - Fetching and processing hydrological data via {@code fetchHydro}
     * - Fetching and processing synoptic data via {@code fetchSynop}
     * </ul>
     *
     * Structured logging at the start and end of the method helps track the
     * execution flow and provides traceability across the data retrieval operations.
     */
    public void fetchAll() {
        log.info("--- START FETCH ALL ---");
        fetchMeteo();
        fetchHydro();
        fetchSynop();
        log.info("--- END FETCH ALL ---");
    }

    /**
     * Fetches and processes meteorological data.
     * This method delegates the fetching and processing of data to the {@code meteoService}.
     * The number of processed records is logged and recorded using the {@code imgwImportLogService}
     * with the {@code METEO} import type. Structured logging is used to indicate
     * the start and end of the operation for traceability.
     */
    public void fetchMeteo() {
        log.info("--- START FETCH METEO ---");
        int count = meteoService.fetchAndProcess();
        imgwImportLogService.recordImport(ImgwImportType.METEO, count);
        log.info("--- END FETCH METEO ---");
    }

    /**
     * Fetches and processes hydro data by delegating the operation to the {@code hydroService}.
     * This method logs the start and end of the execution to provide traceability in logging outputs.
     */
    public void fetchHydro() {
        log.info("--- START FETCH HYDRO ---");
        int count = hydroService.fetchAndProcess();
        imgwImportLogService.recordImport(ImgwImportType.HYDRO, count);
        log.info("--- END FETCH HYDRO ---");
    }

    /**
     * Fetches and processes synoptic data.
     * This method delegates the task of fetching and processing synoptic data
     * to the {@code synopService}. The total number of processed records is
     * logged and subsequently recorded using the {@code imgwImportLogService}
     * with the {@code SYNOP} import type.
     * Structured logging mark the start and end of the operation
     * for easier traceability and debugging.
     */
    public void fetchSynop() {
        log.info("--- START FETCH SYNOP ---");
        int count = synopService.fetchAndProcess();
        imgwImportLogService.recordImport(ImgwImportType.SYNOP, count);
        log.info("--- END FETCH SYNOP ---");
    }
}