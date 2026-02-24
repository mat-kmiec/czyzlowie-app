package pl.czyzlowie.modules.barometer.mapper;

import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.barometer.dto.BarometerViewDto;
import pl.czyzlowie.modules.barometer.entity.PressureTrend;
import pl.czyzlowie.modules.barometer.entity.StationBarometerStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The BarometerViewMapper class is responsible for mapping barometric data from
 * domain objects to Data Transfer Objects (DTOs) used in the user interface layer.
 * This class provides methods for extracting, formatting, and transforming
 * barometric statistics into a user-friendly format, including pressure trends,
 * stability evaluations, and visual indicators.
 *
 * It incorporates rules and thresholds for evaluating barometric stability,
 * generating textual and visual representations of the conditions to provide
 * relevant insights to the user about the current barometric state and its impact.
 *
 * This class is designed to produce instances of {@link BarometerViewDto}
 * that are ready for consumption by the user interface.
 */
@Component
public class BarometerViewMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
    private static final int EXCELLENT_STABILITY_THRESHOLD = 80;
    private static final int AVERAGE_STABILITY_THRESHOLD = 40;

    /**
     * Converts the given station barometer statistics and location name into a {@link BarometerViewDto}.
     * The conversion includes extracting relevant barometric data, determining the pressure trend,
     * evaluating current conditions, formatting the last updated time, and incorporating visual indicators.
     *
     * @param stats an instance of {@link StationBarometerStats} containing barometric statistics such as
     *              current pressure, pressure trends, stability data, and barometric chart data.
     *              This parameter must not be null and should have the necessary fields populated.
     * @param locationName the name of the location corresponding to the station barometer statistics.
     *                     This parameter must not be null.
     * @return a {@link BarometerViewDto} object representing the barometer's data and visualized state
     *         for the given location.
     */
    public BarometerViewDto toDto(StationBarometerStats stats, String locationName) {
        var conditionUI = evaluateCondition(stats);

        return BarometerViewDto.builder()
                .locationName(locationName)
                .currentPressure(stats.getCurrentPressure())
                .trendText(translateTrend(stats.getTrend24h()))
                .trendIcon(getTrendIcon(stats.getTrend24h()))
                .conditionTitle(conditionUI.title())
                .conditionDescription(conditionUI.description())
                .conditionColorClass(conditionUI.colorClass())
                .chartData(stats.getChartData())
                .lastUpdatedTime(formatDate(stats.getLastUpdatedAt()))
                .isFrontApproaching(Boolean.TRUE.equals(stats.getFrontApproaching()))
                .build();
    }

    /**
     * Evaluates the current barometric conditions based on the provided station barometer statistics
     * and returns a {@link ConditionUI} object that summarizes the condition, its description, and
     * a visual indicator.
     *
     * The evaluation considers the following:
     * - If a front is approaching, it warns the user with an alert.
     * - If stability data is unavailable, it informs the user accordingly.
     * - Otherwise, it assesses the pressure stability index and categorizes the condition
     *   as excellent, average, or poor, providing corresponding descriptions and visual cues.
     *
     * @param stats an instance of {@link StationBarometerStats}, containing the barometric data
     *              such as pressure stability index, front approaching status, and other statistics.
     *              Must not be null and should have the relevant fields populated.
     * @return a {@link ConditionUI} object containing the title, descriptive message, and CSS color
     *         class that reflects the evaluated barometric condition.
     */
    private ConditionUI evaluateCondition(StationBarometerStats stats) {
        if (Boolean.TRUE.equals(stats.getFrontApproaching())) {
            return new ConditionUI(
                    "UWAGA: Nadciąga front!",
                    "Zbliża się potężne załamanie pogody i gwałtowny spadek ciśnienia. Drapieżniki mogą wpaść w krótki amok żerowy przed burzą, szykuj najcięższy sprzęt!",
                    "text-danger"
            );
        }

        if (stats.getPressureStabilityIndex() == null) {
            return new ConditionUI(
                    "Brak pełnych danych",
                    "System zbiera dane historyczne do oceny stabilności ciśnienia.",
                    "text-secondary"
            );
        }

        int stability = stats.getPressureStabilityIndex();

        if (stability >= EXCELLENT_STABILITY_THRESHOLD) {
            return new ConditionUI(
                    "Bardzo dobre",
                    "Ciśnienie stabilizuje się na optymalnym poziomie. Pęcherze pławne ryb są ustabilizowane. Drapieżniki, zwłaszcza szczupak i okoń, powinny być bardzo aktywne.",
                    "text-brand-green"
            );
        } else if (stability >= AVERAGE_STABILITY_THRESHOLD) {
            return new ConditionUI(
                    "Przeciętne",
                    "Wahania ciśnienia mogą lekko dezorientować ryby. Warto szukać ich przy dnie lub spowolnić prowadzenie przynęty.",
                    "text-warning"
            );
        } else {
            return new ConditionUI(
                    "Słabe",
                    "Duże skoki ciśnienia. Ryby prawdopodobnie są \"przyklejone\" do dna i niechętnie współpracują. Brania mogą być bardzo delikatne.",
                    "text-danger"
            );
        }
    }

    private String formatDate(LocalDateTime dateTime) {
        return (dateTime != null) ? dateTime.format(DATE_FORMATTER) : "Brak danych";
    }

    /**
     * Translates a given pressure trend into a corresponding descriptive string.
     *
     * @param trend the pressure trend to be translated, represented by a {@code PressureTrend} enumeration.
     *              If null, it returns "Brak danych".
     * @return a string describing the pressure trend:
     *         - "Szybko rosnące" for {@code RISING_FAST}.
     *         - "Lekko rosnące" for {@code RISING}.
     *         - "Stabilne" for {@code STABLE}.
     *         - "Lekko spadające" for {@code FALLING}.
     *         - "Gwałtowny spadek*/
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
     * @param trend the pressure trend for which the icon should be determined.
     *              Can be one of the predefined values of {@code PressureTrend} or null.
     * @return a string representing the icon name:
     *         - "trending-up" for {@code RISING_FAST} or {@code RISING}.
     *         - "minus" for {@code STABLE} or when the {@code trend} is null.
     *         - "trending-down" for {@code FALLING} or {@code FALLING_FAST}.
     */
    private String getTrendIcon(PressureTrend trend) {
        if (trend == null) return "minus";
        return switch (trend) {
            case RISING_FAST, RISING -> "trending-up";
            case STABLE -> "minus";
            case FALLING, FALLING_FAST -> "trending-down";
        };
    }

    /**
     * Represents a condition used to evaluate the current barometric state based on various metrics.
     * This class encapsulates a concise representation of the evaluated condition, including its title,
     * descriptive message, and associated color class.
     *
     * Instances of this class are immutable records constructed with specific details derived
     * during the evaluation of barometric data. Each instance holds:
     * - A textual title of the condition, summarizing the evaluation briefly.
     * - A detailed description explaining the condition and its implications.
     * - A CSS color class to visually distinguish different conditions within the UI.
     *
     * The `ConditionUI` record is utilized in constructing DTOs for the UI layer to present
     * relevant barometric information to end-users in a user-friendly and visually appealing manner.
     */
    private record ConditionUI(String title, String description, String colorClass) {}
}