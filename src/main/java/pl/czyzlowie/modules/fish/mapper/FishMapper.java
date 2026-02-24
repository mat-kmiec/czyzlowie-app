package pl.czyzlowie.modules.fish.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import pl.czyzlowie.modules.fish.dto.FishDetailsDto;
import pl.czyzlowie.modules.fish.dto.FishListElementDto;
import pl.czyzlowie.modules.fish.entity.*;

/**
 * FishMapper is an interface responsible for mapping domain entities to Data Transfer Objects (DTOs)
 * used within the application's fish-related features. It leverages MapStruct for generating the
 * implementation automatically at compile time.
 *
 * Mapping functions:
 * - Converts FishSpecies entities to FishListElementDto for listing simplified fish details.
 * - Converts FishSpecies entities to FishDetailsDto for providing detailed information about fish.
 * - Converts embedded or related entities (e.g., FishHabitat, TackleSetup) into their corresponding
 *   DTO representations used inside FishDetailsDto.
 *
 * Key functionalities:
 * - Maps FishSpecies to FishListElementDto for basic listing purposes.
 * - Maps FishSpecies to FishDetailsDto for comprehensive fish details.
 * - Maps specific embedded properties (e.g., FishHabitat, TackleSetup, PolishRecord, PzwRegulations,
 *   ActivityCalendar, FishAlgorithmParams) into corresponding nested DTOs within FishDetailsDto.
 *
 * Mapping policies:
 * - Uses Spring framework's component model for dependency injection.
 * - Ignores unmapped target properties to prevent configuration errors.
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface FishMapper {

    FishListElementDto toListDto(FishSpecies fish);

    FishDetailsDto toDetailsDto(FishSpecies entity);

    FishDetailsDto.HabitatDto toHabitatDto(FishHabitat habitat);
    FishDetailsDto.TackleDto toTackleDto(TackleSetup tackleSetup);
    FishDetailsDto.RecordDto toRecordDto(PolishRecord record);
    FishDetailsDto.PzwDto toPzwDto(PzwRegulations pzw);
    FishDetailsDto.CalendarDto toCalendarDto(ActivityCalendar calendar);
    FishDetailsDto.AlgorithmDto toAlgorithmDto(FishAlgorithmParams algorithmParams);
}