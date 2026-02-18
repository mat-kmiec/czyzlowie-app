package pl.czyzlowie;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.czyzlowie.modules.moon.service.MoonGlobalBatchService;

@RestController
@RequestMapping("/api/moon")
@RequiredArgsConstructor
public class MoonControlelr {

    private final MoonGlobalBatchService moonGlobalBatchService;

    @PostMapping("/global")
    public ResponseEntity<String> updateMoon() {
        moonGlobalBatchService.generateGlobalDataForYear(2026);
        return ResponseEntity.ok("Zlecono obliczenie danych MOON.");
    }
}
