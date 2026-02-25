package pl.czyzlowie;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.czyzlowie.modules.imgw_api.service.ImgwFetchFacade;


@RestController
@RequestMapping("/api/imgw")
@RequiredArgsConstructor
public class ImgwController {

    private final ImgwFetchFacade fetchService;

    @PostMapping("/synop")
    public ResponseEntity<String> updateSynop() {
        fetchService.fetchSynop();
        return ResponseEntity.ok("Zlecono pobieranie danych SYNOP.");
    }

    @PostMapping("/meteo")
    public ResponseEntity<String> updateMeteo() {
        fetchService.fetchMeteo();
        return ResponseEntity.ok("Zlecono pobieranie danych METEO (Automaty).");
    }

    @PostMapping("/hydro")
    public ResponseEntity<String> updateHydro() {
        fetchService.fetchHydro();
        return ResponseEntity.ok("Zlecono pobieranie danych HYDRO.");
    }

    @PostMapping("/all")
    public ResponseEntity<String> updateAll() {
        fetchService.fetchSynop();
        fetchService.fetchMeteo();
        fetchService.fetchHydro();
        return ResponseEntity.ok("Zlecono pobieranie WSZYSTKICH danych.");
    }
}