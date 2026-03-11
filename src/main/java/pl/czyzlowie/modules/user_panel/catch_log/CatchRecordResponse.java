package pl.czyzlowie.modules.user_panel.catch_log;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents the response model for a fishing catch record.
 * This class is used to transfer data related to a fishing catch event
 * from the server to the client in a structured manner.
 *
 * Each instance contains detailed information about a specific catch event,
 * including the location, time, species captured, and environmental conditions
 * at the time of the catch.
 *
 * The fields in this class map closely to the attributes of the {@code CatchRecord} entity
 * but are customized for API responses.
 *
 * Key attributes include:
 * - The time and place of the catch
 * - The species, weight, and length of the catch
 * - Environmental factors such as air and water temperature, pressure, wind speed, and humidity
 * - Additional contextual data like lure method, moon phase, and user's notes
 *
 * This response model is typically built using the {@code @Builder} annotation, which allows
 * for immutable and flexible object creation.
 */
@Data
@Builder
public class CatchRecordResponse {
    private Long id;
    private LocalDateTime catchDate;
    private String locationName;
    private String photoUrl;
    private String species;
    private BigDecimal weight;
    private Integer length;
    private String lureMethod;
    private String note;
    private BigDecimal airTemperature;
    private BigDecimal pressure;
    private String moonPhase;
    private Integer waterLevel;
    private BigDecimal waterTemperature;
    private BigDecimal humidity;
    private BigDecimal precipitation;
    private BigDecimal windSpeed;
    private String windDirection;
    private BigDecimal discharge;
}
