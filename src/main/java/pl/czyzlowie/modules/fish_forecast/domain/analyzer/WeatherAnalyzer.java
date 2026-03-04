package pl.czyzlowie.modules.fish_forecast.domain.analyzer;

import pl.czyzlowie.modules.fish_forecast.domain.model.FishProfile;
import pl.czyzlowie.modules.fish_forecast.domain.model.WeatherContext;

import java.time.LocalDateTime;

/**
 * Analyzes weather context and fish profiling information to provide predictions
 * or recommendations based on a target time.
 *
 * Implementations of this interface are designed to evaluate specific weather factors,
 * fish behavioral data, or combinations thereof and return an analysis result.
 */
public interface WeatherAnalyzer {
    AnalyzerResult analyze(WeatherContext context, FishProfile profile, LocalDateTime targetTime);
}
