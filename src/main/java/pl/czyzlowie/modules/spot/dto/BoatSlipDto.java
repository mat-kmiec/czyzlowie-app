package pl.czyzlowie.modules.spot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * A data transfer object (DTO) representing detailed information about a boat slip spot.
 * This class extends the {@code SpotDetailsDto} to inherit common properties of a spot
 * and includes specific details relevant to boat slips.
 *
 * The additional attributes specific to boat slips include:
 * - {@code status}: The current status of the boat slip (e.g., available, reserved).
 * - {@code accessType}: The type of access the boat slip offers (e.g., public, private).
 * - {@code feeInfo}: Information about any fees associated with the boat slip.
 * - {@code openingHours}: The hours during which the boat slip is accessible.
 * - {@code surfaceType}: The type of surface of the boat slip (e.g., concrete, gravel).
 * - {@code incline}: The incline of the boat slip.
 * - {@code unitLimit}: Limitations on the units or weight that can use the boat slip.
 * - {@code endDepth}: The depth at the end of the boat slip.
 * - {@code trailerParking}: Information on the availability of trailer parking.
 * - {@code hasMooringPier}: Indicates whether the boat slip has a mooring pier.
 * - {@code lightingAndMonitoring}: Details of the lighting and monitoring systems.
 * - {@code maneuveringSpace}: Information about maneuvering space availability.
 * - {@code navigationalAlerts}: Any navigational alerts or restrictions specified.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BoatSlipDto extends SpotDetailsDto {
    private String status;
    private String accessType;
    private String feeInfo;
    private String openingHours;
    private String surfaceType;
    private String incline;
    private String unitLimit;
    private Double endDepth;
    private String trailerParking;
    private Boolean hasMooringPier;
    private String lightingAndMonitoring;
    private String maneuveringSpace;
    private String navigationalAlerts;
}