package pl.czyzlowie.modules.map.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a boat slip, an entity derived from the MapSpot base class,
 * specifically used to model locations designated for boat launching or docking.
 *
 * This class is mapped to the "map_slips" table in the database, with a discriminator
 * value of "SLIP". It includes various properties defining the characteristics and
 * functionalities of a boat slip.
 *
 * Attributes:
 * - `status`: Current status of the boat slip (e.g., open, closed).
 * - `accessType`: Type of access provided at the slip (e.g., public, private).
 * - `feeInfo`: Information about any associated fees for using the slip.
 * - `openingHours`: Operational hours during which the slip is accessible.
 * - `surfaceType`: Description of the surface material of the slip (e.g., concrete, gravel).
 * - `incline`: Specifies the incline of the slip for launching or docking.
 * - `unitLimit`: Indicates any size or weight restrictions for boats.
 * - `endDepth`: Depth of water at the end of the slip, relevant for launching.
 * - `trailerParking`: Availability or description of trailer parking options.
 * - `hasMooringPier`: Indicates if the slip includes a mooring pier.
 * - `lightingAndMonitoring`: Details regarding lighting and monitoring facilities.
 * - `maneuveringSpace`: Information about available space for maneuvering trailers or boats.
 * - `navigationalAlerts`: Text field containing any navigational alerts or warnings relevant to the area.
 */
@Entity
@Table(name = "map_slips")
@DiscriminatorValue("SLIP")
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