package pl.czyzlowie.modules.barometer.mapper;

import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.barometer.dto.BarometerViewDto;
import pl.czyzlowie.modules.barometer.entity.PressureTrend;
import pl.czyzlowie.modules.barometer.entity.StationBarometerStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class BarometerViewMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

    public BarometerViewDto toDto(StationBarometerStats stats, String locationName) {
        return BarometerViewDto.builder()
                .locationName(locationName)
                .currentPressure(stats.getCurrentPressure())
                .trendText(translateTrend(stats.getTrend24h()))
                .trendIcon(getTrendIcon(stats.getTrend24h()))
                .conditionTitle(getConditionTitle(stats))
                .conditionDescription(getConditionDescription(stats))
                .conditionColorClass(getConditionColor(stats))
                .chartData(stats.getChartData())
                .lastUpdatedTime(formatDate(stats.getLastUpdatedAt()))
                .isFrontApproaching(Boolean.TRUE.equals(stats.getFrontApproaching()))
                .build();
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return "Przed chwilą";
        return dateTime.format(DATE_FORMATTER);
    }

    private String translateTrend(PressureTrend trend) {
        if (trend == null) return "Brak danych";
        return switch (trend) {
            case RISING_FAST -> "Szybko rosnące";
            case RISING -> "Lekko rosnące";
            case STABLE -> "Stabilne";
            case FALLING -> "Lekko spadające";
            case FALLING_FAST -> "Gwałtowny spadek";
        };
    }

    private String getTrendIcon(PressureTrend trend) {
        if (trend == null) return "minus";
        return switch (trend) {
            case RISING_FAST, RISING -> "trending-up";
            case STABLE -> "minus";
            case FALLING, FALLING_FAST -> "trending-down";
        };
    }

    private String getConditionTitle(StationBarometerStats stats) {
        if (Boolean.TRUE.equals(stats.getFrontApproaching())) return "UWAGA: Nadciąga front!";
        if (stats.getPressureStabilityIndex() == null) return "Brak pełnych danych";

        int stability = stats.getPressureStabilityIndex();
        if (stability > 80) return "Bardzo dobre";
        if (stability > 40) return "Przeciętne";
        return "Słabe";
    }

    private String getConditionColor(StationBarometerStats stats) {
        if (Boolean.TRUE.equals(stats.getFrontApproaching())) return "text-danger";
        if (stats.getPressureStabilityIndex() == null) return "text-secondary";

        int stability = stats.getPressureStabilityIndex();
        if (stability > 80) return "text-brand-green";
        if (stability > 40) return "text-warning";
        return "text-danger";
    }

    private String getConditionDescription(StationBarometerStats stats) {
        if (Boolean.TRUE.equals(stats.getFrontApproaching())) {
            return "Zbliża się potężne załamanie pogody i gwałtowny spadek ciśnienia. Drapieżniki mogą wpaść w krótki amok żerowy przed burzą, szykuj najcięższy sprzęt!";
        }

        int stability = stats.getPressureStabilityIndex() != null ? stats.getPressureStabilityIndex() : 0;
        if (stability > 80) {
            return "Ciśnienie stabilizuje się na optymalnym poziomie. Pęcherze pławne ryb są ustabilizowane. Drapieżniki, zwłaszcza szczupak i okoń, powinny być bardzo aktywne.";
        } else if (stability > 40) {
            return "Wahania ciśnienia mogą lekko dezorientować ryby. Warto szukać ich przy dnie lub spowolnić prowadzenie przynęty.";
        } else {
            return "Duże skoki ciśnienia. Ryby prawdopodobnie są \"przyklejone\" do dna i niechętnie współpracują. Brania mogą być bardzo delikatne.";
        }
    }
}