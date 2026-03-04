package pl.czyzlowie.modules.fish_forecast.domain.analyzer;

import lombok.Builder;

import java.util.List;

/**
 * Represents the result of an analysis performed by a specific weather or environmental analyzer.
 * This record is used to encapsulate the key outcomes of the analysis, including the analyzer's name,
 * a computed score, the weight of the analysis, the dominant influencing factor, and suggested tips
 * for improving outcomes based on the analysis.
 *
 * Fields:
 * - `analyzerName`: The name of the analyzer that produced the result.
 * - `score`: The calculated score indicating the quality or suitability of conditions analyzed.
 * - `weight`: The weight or influence factor of the analyzer's assessment in the overall evaluation.
 * - `dominantFactor`: A string describing the primary factor influencing the analysis result.
 * - `tackleTips`: A list of tips or recommendations for tackling potential challenges or optimizing outcomes.
 */
@Builder
public record AnalyzerResult(
        String analyzerName,
        double score,
        int weight,
        String dominantFactor,
        List<String> tackleTips
) {}