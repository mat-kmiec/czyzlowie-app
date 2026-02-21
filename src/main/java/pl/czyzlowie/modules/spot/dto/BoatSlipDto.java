package pl.czyzlowie.modules.spot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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