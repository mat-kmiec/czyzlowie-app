package pl.czyzlowie.modules.fish_forecast.domain.model;

import lombok.Builder;
import pl.czyzlowie.modules.fish.entity.ActivityCalendar;
import pl.czyzlowie.modules.fish.entity.FishAlgorithmParams;

@Builder
public record FishProfile(
        Long id,
        String name,
        boolean isGeneralBiomass,
        ActivityCalendar calendar,
        FishAlgorithmParams params
) {}
