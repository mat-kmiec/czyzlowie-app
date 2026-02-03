package pl.czyzlowie.modules.imgw.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class ImgwFetchFacade {

    private final ImgwMeteoFetchService meteoService;
    private final ImgwHydroFetchService hydroService;
    private final ImgwSynopFetchService synopService;

    public void fetchAll() {
        log.info("--- START FETCH ALL ---");
        fetchMeteo();
        fetchHydro();
        fetchSynop();
        log.info("--- END FETCH ALL ---");
    }

    public void fetchMeteo() {
        meteoService.fetchAndProcess();
    }

    public void fetchHydro() {
        hydroService.fetchAndProcess();
    }

    public void fetchSynop() {
        synopService.fetchAndProcess();
    }
}