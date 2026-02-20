package pl.czyzlowie.modules.map.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "map_slips")
@Getter
@Setter
public class BoatSlip extends MapSpot {

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

    @Column(columnDefinition = "TEXT")
    private String navigationalAlerts;
}