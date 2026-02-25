package pl.czyzlowie.modules.barometer.mapper;

import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.barometer.dto.BarometerChartData;
import pl.czyzlowie.modules.barometer.dto.BarometerViewDto;
import pl.czyzlowie.modules.barometer.entity.PressureTrend;
import pl.czyzlowie.modules.barometer.entity.StationBarometerStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class BarometerViewMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

    private static final int EXCELLENT_STABILITY_THRESHOLD = 75;
    private static final int AVERAGE_STABILITY_THRESHOLD = 45;
    private static final double SIGNIFICANT_CHANGE_THRESHOLD = 7.0;
    private static final double FORECAST_CHANGE_THRESHOLD = 5.0;

    public BarometerViewDto toDto(StationBarometerStats stats, String locationName) {
        var conditionUI = evaluateCondition(stats);
        var forecastUI = evaluateForecast(stats);

        return BarometerViewDto.builder()
                .locationName(locationName)
                .currentPressure(stats.getCurrentPressure())
                .trendText(translateTrend(stats.getTrend24h()))
                .trendIcon(getTrendIcon(stats.getTrend24h()))
                .conditionTitle(conditionUI.title())
                .conditionDescription(conditionUI.description())
                .conditionColorClass(conditionUI.colorClass())
                .isFrontApproaching(Boolean.TRUE.equals(stats.getFrontApproaching()))
                .chartData(stats.getChartData())
                .lastUpdatedTime(formatDate(stats.getLastUpdatedAt()))
                .build();
    }

    private ConditionUI evaluateCondition(StationBarometerStats stats) {
        if (Boolean.TRUE.equals(stats.getFrontApproaching())) {
            return new ConditionUI(
                    "AMOK PRZEDBURZOWY!",
                    "Gwałtowne tąpnięcie ciśnienia! Ryby czują zbliżający się front. Drapieżniki mogą wpadać w krótkie, agresywne żerowanie. Szykuj najmocniejszy zestaw, to może być godzina życia!",
                    "text-danger"
            );
        }

        if (stats.getPressureStabilityIndex() == null) {
            return new ConditionUI("Analiza danych...", "System kalibruje lokalne warunki barometryczne.", "text-secondary");
        }

        int stability = stats.getPressureStabilityIndex();
        double delta = stats.getDelta24h().doubleValue();
        double currentP = stats.getCurrentPressure().doubleValue();

        if (stability >= EXCELLENT_STABILITY_THRESHOLD && currentP > 1020) {
            return new ConditionUI(
                    "Stabilny Wyż – Czyste Niebo",
                    "Wysokie ciśnienie ustabilizowało pęcherze pławne. Bardzo dobre warunki na okonia i szczupaka. Szukaj ryb nieco głębiej, przy stokach i strukturach. Precyzyjne prowadzenie będzie kluczem.",
                    "text-brand-green"
            );
        }

        if (stability >= EXCELLENT_STABILITY_THRESHOLD && currentP <= 1020) {
            return new ConditionUI(
                    "Stabilizacja – Czas na Białą Rybę",
                    "Ciśnienie niskie, ale stabilne. Idealny czas na leszcza, lina i karpia. Ryby mogą być aktywne na płyciznach. Drapieżniki mogą żerować ospale, warto postawić na większą przynętę i wolne tempo.",
                    "text-brand-green"
            );
        }

        if (stability >= AVERAGE_STABILITY_THRESHOLD) {
            String trendHint = delta > 0 ? "Lekki wzrost sprzyja drapieżnikom." : "Lekki spadek pobudza żerowanie.";
            return new ConditionUI(
                    "Warunki Przeciętne",
                    "Ryby mogą być nieco wybredne. " + trendHint + " Warto często zmieniać kolory przynęt i eksperymentować z głębokością.",
                    "text-warning"
            );
        }


        if (delta > SIGNIFICANT_CHANGE_THRESHOLD) {
            return new ConditionUI(
                    "Silny Wzrost – Ryby „Przyklejone”",
                    "Gwałtowny wzrost ciśnienia „dociska” ryby do dna. Są apatyczne i potrzebują czasu na aklimatyzację. Szukaj ich w najgłębszych miejscach. Tylko bardzo wolne prowadzenie może przynieść branie.",
                    "text-danger"
            );
        }

        if (delta < -SIGNIFICANT_CHANGE_THRESHOLD) {
            return new ConditionUI(
                    "Silny Spadek – Deorientacja",
                    "Ciśnienie szybko ucieka. Ryby mogą masowo opuszczać głębokie rewiry. Szukaj ich w toni (pelagicznie). Brania mogą być bardzo delikatne, ledwo wyczuwalne na kiju.",
                    "text-danger"
            );
        }

        return new ConditionUI(
                "Barometryczny Rollercoaster",
                "Ciśnienie szaleje. Ryby są rozproszone i zdezorientowane. Jeśli musisz być nad wodą, szukaj miejsc z dużą ilością tlenu i przepływem wody.",
                "text-muted"
        );
    }

    private ConditionUI evaluateForecast(StationBarometerStats stats) {
        if (Boolean.TRUE.equals(stats.getFrontApproaching())) {
            return new ConditionUI("Gwałtowna Zmiana", "Prognozowane tąpnięcie ciśnienia. Przygotuj się na nagłe załamanie pogody.", "text-danger");
        }

        double fDelta = calculateForecastDelta24h(stats);

        if (fDelta < -FORECAST_CHANGE_THRESHOLD) {
            return new ConditionUI("Nadchodzi Niż", "Ciśnienie zacznie wyraźnie spadać. To często zwiastuje poprawę żerowania drapieżników w najbliższym czasie.", "text-warning");
        } else if (fDelta > FORECAST_CHANGE_THRESHOLD) {
            return new ConditionUI("Buduje się Wyż", "Prognozowany silny wzrost ciśnienia. Ryby mogą stać się mniej aktywne w nadchodzącej dobie.", "text-warning");
        }

        return new ConditionUI("Stabilny Horyzont", "W nadchodzących dniach nie przewidujemy nagłych zmian. Ciśnienie pozostanie na zbliżonym poziomie.", "text-brand-green");
    }

    private double calculateForecastDelta24h(StationBarometerStats stats) {
        if (stats.getChartData() == null || stats.getChartData().getForecast24h() == null) return 0.0;

        List<BarometerChartData.DataPoint> forecast = stats.getChartData().getForecast24h();
        if (forecast.size() < 2) return 0.0;

        double current = stats.getCurrentPressure().doubleValue();
        double future = forecast.get(forecast.size() - 1).getP().doubleValue();

        return future - current;
    }

    private String formatDate(LocalDateTime dateTime) {
        return (dateTime != null) ? dateTime.format(DATE_FORMATTER) : "Brak danych";
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

    private record ConditionUI(String title, String description, String colorClass) {}
}