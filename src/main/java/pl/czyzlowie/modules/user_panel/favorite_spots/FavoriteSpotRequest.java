package pl.czyzlowie.modules.user_panel.favorite_spots;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * Represents a request to create or update a favorite fishing spot.
 * This class is used to transfer user input for processing within the application.
 *
 * Fields:
 * - spotName: The name of the fishing spot. This is a required field.
 * - location: The location description or address of the fishing spot. Optional.
 * - lat: Latitude of the spot's geographical position. Must be between -90.0 and 90.0. This is a required field.
 * - lng: Longitude of the spot's geographical position. Must be between -180.0 and 180.0. This is a required field.
 * - waterType: The type of waterbody where the spot is located, e.g., river, lake, pond, commercial, or sea. This is a required field.
 * - fishTags: A list of tags representing fish species or characteristics associated with the spot. Optional.
 * - note: Additional notes about the spot. Maximum length is 500 characters. Optional.
 *
 * Annotations:
 * - Validations are provided using annotations to ensure proper input for required fields, lengths, and value ranges.
 * - Lombok's @Data is used for automatic generation of getters, setters, and other utility methods.
 */
@Data
public class FavoriteSpotRequest {

    @NotBlank(message = "Nazwa jest wymagana")
    private String spotName;

    private String location;

    @NotNull(message = "Szerokość geograficzna jest wymagana")
    @DecimalMin("-90.0") @DecimalMax("90.0")
    private BigDecimal lat;

    @NotNull(message = "Długość geograficzna jest wymagana")
    @DecimalMin("-180.0") @DecimalMax("180.0")
    private BigDecimal lng;

    @NotBlank(message = "Typ akwenu jest wymagany")
    private String waterType;

    private List<String> fishTags;


    @Size(max = 500, message = "Notatka może mieć maksymalnie 500 znaków")
    private String note;
}