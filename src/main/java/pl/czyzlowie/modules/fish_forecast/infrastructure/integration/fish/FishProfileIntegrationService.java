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

/**
 * Service responsible for integration operations related to fish profiles.
 * Provides functionality to fetch target fish profiles for analysis based
 * on specified criteria or default values.
 *
 * The service uses asynchronous processing for data retrieval and transformation
 * into domain models.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FishProfileIntegrationService {

    private final FishSpeciesRepository fishRepository;

    /**
     * Fetches fish profiles based on a list of target fish species identifiers.
     * If the list is null or empty, a general biomass profile is created and returned.
     *
     * This method is executed asynchronously using a specified executor.
     *
     * @param targetFishIds a list of integer identifiers for target fish species; can be null or empty
     * @return a CompletableFuture containing a list of FishProfile objects, which may represent
     *         specific fish species or a general biomass profile if no species were specified
     */
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

    /**
     * Maps a FishSpecies entity into a FishProfile domain model.
     *
     * @param entity the FishSpecies entity to be mapped
     * @return a FishProfile object containing the mapped data from the provided entity
     */
    private FishProfile mapToProfile(FishSpecies entity) {
        return FishProfile.builder()
                .id(entity.getId())
                .name(entity.getName())
                .isGeneralBiomass(false)
                .category(entity.getCategory())
                .calendar(entity.getActivityCalendar())
                .params(entity.getAlgorithmParams())
                .build();
    }

    /**
     * Creates a general biomass profile for fish, representing an aggregate activity
     * profile based on general environmental preferences. This profile is not specific
     * to any particular fish species and is intended for analyzing general fish activity
     * trends.
     *
     * @return a FishProfile object encapsulating the general biomass profile with default
     *         parameters for environmental activity preferences.
     */
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
                .category(null)
                .calendar(null)
                .params(generalParams)
                .build();
    }
}
