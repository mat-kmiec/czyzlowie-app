package pl.czyzlowie.modules.spot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * A data transfer object (DTO) representing detailed information about a specific fishing spot.
 * This class extends the {@code SpotDetailsDto} to inherit common properties of a spot
 * and includes specific details relevant to fishing activities at a particular location.
 *
 * The additional attributes specific to a specific fishing spot include:
 * - {@code dimensionInfo}: Information about the dimensions or size of the fishing spot.
 * - {@code parentWaterType}: The type of the water body in which the fishing spot resides (e.g., lake, river).
 * - {@code localDepth}: The depth of the water at this specific location.
 * - {@code localBottomType}: The type of bottom surface at this specific location (e.g., sandy, rocky).
 * - {@code localCurrent}: Information about the water current at this specific location.
 * - {@code standsCondition}: The condition of any fishing stands or facilities at the spot.
 * - {@code effectiveMethods}: A description of effective fishing methods recommended for this spot.
 * - {@code bestTimeAndBaits}: Suggestions on the best times to fish and recommended baits to use at the spot.
 * - {@code fishingPressure}: An assessment of the fishing pressure at the spot (e.g., low, medium, high).
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SpecificSpotDto extends SpotDetailsDto {
    private String dimensionInfo;
    private String parentWaterType;
    private Double localDepth;
    private String localBottomType;
    private String localCurrent;
    private String standsCondition;
    private String effectiveMethods;
    private String bestTimeAndBaits;
    private String fishingPressure;
}