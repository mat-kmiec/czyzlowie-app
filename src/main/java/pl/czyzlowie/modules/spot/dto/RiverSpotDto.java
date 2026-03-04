package pl.czyzlowie.modules.spot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * A data transfer object (DTO) representing detailed information about a river fishing spot.
 * This class extends {@code SpotDetailsDto} to inherit common properties of a spot
 * and includes specific details pertinent to river environments.
 *
 * The additional attributes specific to river spots include:
 * - {@code riverType}: The classification of the river (e.g., mountain stream, lowland river).
 * - {@code channelCharacter}: The physical characteristics of the river's channel (e.g., braided, meandering).
 * - {@code avgWidth}: The average width of the river in meters.
 * - {@code avgDepth}: The average depth of the river in meters.
 * - {@code bottomType}: The type of the riverbed (e.g., sandy, gravelly, rocky).
 * - {@code waterStructures}: Information about water structures like weirs, dams, or bridges affecting the river.
 * - {@code dominantSpecies}: The primary species of fish or aquatic life dominant in the river.
 * - {@code fishRegion}: The fishing region or zone associated with the river.
 * - {@code specialSections}: Any sections or areas of the river designated for special regulations (e.g., catch-and-release zones).
 * - {@code methodBans}: Fishing methods or techniques that are prohibited in the river.
 * - {@code boatFishingAllowed}: Indicates whether fishing from a boat is permitted on the river.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RiverSpotDto extends SpotDetailsDto {
    private String riverType;
    private String channelCharacter;
    private Double avgWidth;
    private Double avgDepth;
    private String bottomType;
    private String waterStructures;
    private String dominantSpecies;
    private String fishRegion;
    private String specialSections;
    private String methodBans;
    private Boolean boatFishingAllowed;
}
