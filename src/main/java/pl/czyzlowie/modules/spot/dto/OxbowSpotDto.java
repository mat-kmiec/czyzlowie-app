package pl.czyzlowie.modules.spot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * A data transfer object (DTO) representing detailed information about an oxbow spot.
 * This class extends {@code SpotDetailsDto} to inherit common spot properties
 * and includes specific details related to oxbow lakes.
 *
 * The additional attributes specific to oxbow spots include:
 * - {@code areaHectares}: The area of the oxbow in hectares.
 * - {@code avgDepth}: The average depth of the oxbow in meters.
 * - {@code maxDepth}: The maximum depth of the oxbow in meters.
 * - {@code riverConnection}: Information about the connection of the oxbow to a river.
 * - {@code siltingLevel}: The level of silting in the oxbow.
 * - {@code overgrowthLevel}: The level of vegetation overgrowth.
 * - {@code oxygenDepletionRisk}: Indicates whether there is a risk of oxygen depletion.
 * - {@code driesUp}: Indicates whether the oxbow dries up periodically.
 * - {@code dominantFish}: The dominant fish species found in the oxbow.
 * - {@code shoreAccess}: Details about access to the shoreline of the oxbow.
 * - {@code wadersRequired}: Indicates whether waders are necessary to fish in the oxbow.
 * - {@code snagsLevel}: The level of snags (obstructions) present in the oxbow.
 * - {@code bestSeasons}: The recommended seasons to visit or fish in the oxbow.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OxbowSpotDto extends SpotDetailsDto {
    private Double areaHectares;
    private Double avgDepth;
    private Double maxDepth;
    private String riverConnection;
    private String siltingLevel;
    private String overgrowthLevel;
    private Boolean oxygenDepletionRisk;
    private Boolean driesUp;
    private String dominantFish;
    private String shoreAccess;
    private Boolean wadersRequired;
    private String snagsLevel;
    private String bestSeasons;
}