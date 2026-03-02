package pl.czyzlowie.modules.fish_forecast.infrastructure.integration.fish;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.fish.entity.enums.PressureTrend;
import pl.czyzlowie.modules.fish.entity.FishAlgorithmParams;
import pl.czyzlowie.modules.fish.entity.FishSpecies;
import pl.czyzlowie.modules.fish.entity.enums.WaterLevelTrend;
import pl.czyzlowie.modules.fish.repository.FishSpeciesRepository;
import pl.czyzlowie.modules.fish_forecast.domain.model.FishProfile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class FishProfileIntegrationService {

    private final FishSpeciesRepository fishRepository;

    @Async("dataFetchExecutor")
    public CompletableFuture<List<FishProfile>> fetchTargetProfiles(List<Integer> targetFishIds) {

        if (targetFishIds == null || targetFishIds.isEmpty()) {
            log.info("Brak zdefiniowanych gatunków. Inicjalizacja trybu Ogólnej Biomasy.");
            return CompletableFuture.completedFuture(List.of(createGeneralBiomassProfile()));
        }

        List<Long> ids = targetFishIds.stream().map(Integer::longValue).toList();
        List<FishSpecies> speciesList = fishRepository.findByIdIn(ids);

        List<FishProfile> profiles = speciesList.stream()
                .map(this::mapToProfile)
                .toList();

        log.info("Pobrano {} profili gatunkowych do analizy.", profiles.size());
        return CompletableFuture.completedFuture(profiles);
    }

    private FishProfile mapToProfile(FishSpecies entity) {
        return FishProfile.builder()
                .id(entity.getId())
                .name(entity.getName())
                .isGeneralBiomass(false)
                .calendar(entity.getActivityCalendar())
                .params(entity.getAlgorithmParams())
                .build();
    }

    private FishProfile createGeneralBiomassProfile() {
        FishAlgorithmParams generalParams = FishAlgorithmParams.builder()
                .preferredPressureTrend(PressureTrend.STABLE_HIGH)
                .preferredWaterLevelTrend(WaterLevelTrend.STABLE)
                .weightPressure(100)
                .weightWaterLevel(50)
                .weightWind(50)
                .weightWaterTemp(0)
                .build();

        return FishProfile.builder()
                .id(-1L)
                .name("Ogólna Aktywność Ryb")
                .isGeneralBiomass(true)
                .calendar(null)
                .params(generalParams)
                .build();
    }
}
