package pl.czyzlowie.modules.imgw_api.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.imgw_api.service.ImgwDataCleanupService;

@Component
@RequiredArgsConstructor
public class ImgwCleanupScheduler {

    private final ImgwDataCleanupService cleanupService;

    @Scheduled(cron = "0 30 1 * * *")
    public void scheduleDataCleanup() {
        cleanupService.cleanupOldData(7);
    }
}
