package pl.czyzlowie.modules.barometer.mapper;

import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.barometer.dto.BarometerChartData;
import pl.czyzlowie.modules.barometer.dto.BarometerViewDto;
import pl.czyzlowie.modules.barometer.entity.PressureTrend;
import pl.czyzlowie.modules.barometer.entity.StationBarometerStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * A class responsible for mapping barometric data from {@link StationBarometerStats}
 * and other related information such as location name into a {@link BarometerViewDto} object.
 *
 * The class evaluates multiple aspects, including current pressure, pressure trends,
 * stability, and forecasted changes, to provide summarized conditions for the user.
 *
 * Includes methods to:
 * - Transform station barometer statistics into a data transfer object.
 * - Analyze and assign user-friendly condition titles, descriptions, and styles based on the data.
 * - Evaluate pressure trends, forecast changes, and pressure stability indices.
 * - Format dates and translate pressure trends to user-readable formats.
 */
@Component
public class BarometerViewMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

    private static final int EXCELLENT_STABILITY_THRESHOLD = 75;
    private static final int AVERAGE_STABILITY_THRESHOLD = 45;
    private static final double SIGNIFICANT_CHANGE_THRESHOLD = 7.0;
    private static final double FORECAST_CHANGE_THRESHOLD = 5.0;

    /**
     * Converts the data from a StationBarometerStats object and location name into a BarometerViewDto.
     *
     * @param stats        the StationBarometerStats object containing barometric data and trends.
     * @param locationName the name of the location associated with the barometric data.
     * @return a BarometerViewDto object constructed using the provided barometric data and location name.
     */
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

    /**
     * Evaluates the current barometric conditions using the data in the provided StationBarometerStats
     * and creates a ConditionUI object representing the observed weather condition and associated details.
     *
     * @param stats the StationBarometerStats object containing barometric data such as current pressure,
     *              24-hour pressure delta, stability index, and front approaching status.
     * @return a ConditionUI object encapsulating the title, description, and CSS class indicating
     *         the evaluated barometric weather condition.
     */
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

    /**
     * Evaluates the barometric forecast conditions based on the provided station barometer statistics.
     * Generates a ConditionUI object carrying information about the forecasted weather pattern including
     * its title, description, and a representative CSS class.
     *
     * @param stats the StationBarometerStats object containing barometric data and forecast trends,
     *              such as the status of an approaching front and forecasted pressure changes.
     * @return a ConditionUI object that describes the forecasted condition, including a title,
     *         descriptive text, and CSS style information.
     */
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

    /**
     * Calculates the delta between the current atmospheric pressure and the forecasted
     * atmospheric pressure for the next 24 hours.
     *
     * This method extracts the current pressure and the pressure value from the last
     * data point in the 24-hour forecast, then computes the difference (future - current).
     * If the necessary data is unavailable, it returns 0.0.
     *
     * @param stats the {@code StationBarometerStats} object containing current pressure
     *              and 24-hour forecasted pressure data.
     * @return the difference between the forecasted pressure and current pressure
     *         as a {@code double}, or 0.0 if data is insufficient or unavailable.
     */
    private double calculateForecastDelta24h(StationBarometerStats stats) {
        if (stats.getChartData() == null || stats.getChartData().getForecast24h() == null) return 0.0;

        List<BarometerChartData.DataPoint> forecast = stats.getChartData().getForecast24h();
        if (forecast.size() < 2) return 0.0;

        double current = stats.getCurrentPressure().doubleValue();
        double future = forecast.get(forecast.size() - 1).getP().doubleValue();

        return future - current;
    }

    /**
     * Formats the given LocalDateTime object into a string representation
     * using a predefined date format. If the input dateTime is null,
     * it returns a default string indicating no data.
     *
     * @param dateTime the LocalDateTime object to be formatted; may be null
     * @return a string representing the formatted date, or "Brak danych" if dateTime is null
     */
    private String formatDate(LocalDateTime dateTime) {
        return (dateTime != null) ? dateTime.format(DATE_FORMATTER) : "Brak danych";
    }

    /**
     * Translates a given pressure trend into a localized string representation.
     *
     * This method provides a human-readable description of the specified {@code PressureTrend} value.
     * If the input trend is {@code null}, a default message indicating lack of data is returned.
     *
     * @param trend the {@code PressureTrend} enum value representing the barometric pressure trend.
     *              Possible values include:
     *              {@code RISING_FAST}, {@code RISING}, {@code STABLE}, {@code FALLING}, {@code FALLING_FAST}.
     * @return a localized string that describes the specified pressure trend, or "Brak danych" if the trend is {@code null}.
     */
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

    /**
     * Determines the appropriate trend icon based on the given pressure trend.
     *
     * @param trend the pressure trend, which can be null or one of the defined trend states such as RISING, STABLE, or FALLING
     * @return a string representing the icon name corresponding to the trend; for example, "trending-up" for rising, "minus" for stable, or "trending-down" for falling
     */
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