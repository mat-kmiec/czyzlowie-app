package pl.czyzlowie.modules.user_panel.trip_calendar;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper interface for mapping between {@link TripCalendar}, {@link TripCalendarDto}, and
 * {@link CreateTripCalendarRequest}.
 *
 * This mapper is used to transform data between the entity and its corresponding DTOs or request objects,
 * facilitating operations like conversion for API responses or handling incoming requests.
 *
 * The mapper is implemented automatically by the MapStruct library, using the defined mapping rules and
 * conventions. It is annotated with @Mapper to indicate its role and configuration within a Spring context.
 *
 * Configuration:
 * - componentModel: Configured as "spring" to enable detection and usage as a Spring Bean.
 * - unmappedTargetPolicy: Set to IGNORE, ensuring unmapped properties do not cause exceptions.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TripCalendarMapper {

    TripCalendarDto toDto(TripCalendar tripCalendar);

    TripCalendar toEntity(CreateTripCalendarRequest request);
}
