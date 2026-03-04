package pl.czyzlowie.modules.fish_forecast.domain.engine;

import lombok.Builder;
import pl.czyzlowie.modules.fish_forecast.domain.model.HydroSnapshot;
import pl.czyzlowie.modules.fish_forecast.domain.model.MoonSnapshot;
import pl.czyzlowie.modules.fish_forecast.domain.model.SynopSnapshot;
import pl.czyzlowie.modules.fish_forecast.domain.model.WeatherContext;

import java.util.List;

/**
 * Represents the comprehensive result of a global forecast, combining meteorological,
 * hydrological, lunar, and angling-specific data to facilitate analysis and prediction.
 *
 * This record integrates diverse datasets such as chart trend data, snapshots of both
 * current and forecasted environmental states, and aggregated contextual details.
 * It serves as the central output for multi-dimensional forecasting engines designed
 * for applications like angling conditions, fish activity prediction, and general weather
 * insights.
 *
 * Components:
 * - pressureChart: Represents the historical or forecasted pressure trend as a series of chart data points.
 * - airTempChart: Represents the historical or forecasted air temperature trend as a series of chart data points.
 * - windChart: Represents the historical or forecasted wind data trend as a series of chart data points.
 * - currentSynop: Contains the latest synoptic weather data snapshot.
 * - currentHydro: Contains the latest hydrological data snapshot including water-level trends.
 * - forecast24h: A collection of predicted synoptic weather states for the next 24 hours.
 * - moonForecast3d: A 3-day lunar forecast including phases, illumination percentages, and local events.
 * - currentMoonData: Contains the lunar and solar data for the current day.
 * - waterLevelTrend: Qualitative description of water level changes (e.g., rising, stable, falling).
 * - waterTempTrend: Qualitative description of water temperature changes.
 * - generalBiteIndex: Computed score reflecting the overall likelihood of fish activity, influenced by a combination of environmental factors.
 * - anglingPressureLevel: Descriptive representation of atmospheric pressure changes (e.g., high, low, rapid fluctuations).
 * - speciesReports: A series of tactical reports per species, providing specific conditions and insights relevant to fishing.
 * - fullContext: Aggregated and structured weather context providing a detailed overview of meteorological and hydrological conditions.
 */
@Builder
public record GlobalForecastResult(
        List<ChartDataPoint> pressureChart,
        List<ChartDataPoint> airTempChart,
        List<ChartDataPoint> windChart,
        SynopSnapshot currentSynop,
        HydroSnapshot currentHydro,
        List<SynopSnapshot> forecast24h,
        List<MoonSnapshot> moonForecast3d,
        MoonSnapshot currentMoonData,
        String waterLevelTrend,
        String waterTempTrend,
        double generalBiteIndex,
        String anglingPressureLevel,
        List<SpeciesTacticalReport> speciesReports,
        WeatherContext fullContext
) {}