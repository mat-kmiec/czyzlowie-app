package pl.czyzlowie.modules.user_panel.catch_log;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a data transfer object for creating a new fishing catch record.
 * This class is used to encapsulate the necessary fields and validation rules
 * required when submitting a request to create a new catch record.
 *
 * Fields included in this class:
 * - Capture date and time
 * - Location details
 * - Geographic coordinates (latitude and longitude)
 * - Species of the caught fish
 * - Weight of the catch in kilograms
 * - Length of the catch in centimeters
 * - Lure or bait information
 * - Additional notes or comments about the catch
 * - Flags for ignoring hydrological and telemetry data
 *
 * Validation annotations are applied to enforce business rules, such as:
 * - Non-null and non-blank fields for mandatory attributes
 * - Constraints on numerical values (e.g., weight and length)
 * - Maximum allowable lengths for string values like lure and notes
 *
 * This class is part of a request-response workflow for persisting
 * user-specified data related to recorded catches.
 */
@Data
public class CatchRecordCreateRequest {

    @NotNull(message = "Data połowu jest wymagana")
    @PastOrPresent(message = "Data połowu nie może być z przyszłości")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime catchDate;

    @NotBlank(message = "Lokalizacja jest wymagana")
    private String location;

    private BigDecimal lat;
    private BigDecimal lng;

    @NotBlank(message = "Gatunek jest wymagany")
    private String species;

    @DecimalMin(value = "0.0", message = "Waga nie może być ujemna")
    @DecimalMax(value = "100.0", message = "Waga maksymalna to 100 kg")
    private BigDecimal weight;

    @Min(value = 0, message = "Długość nie może być ujemna")
    @Max(value = 300, message = "Długość maksymalna to 300 cm")
    private Integer length;

    @Size(max = 255, message = "Przynęta może mieć maksymalnie 255 znaków")
    private String lure;

    @Size(max = 500, message = "Notatka może mieć maksymalnie 500 znaków")
    private String note;

    private boolean ignoreHydro;
    private boolean ignoreTelemetry;
}