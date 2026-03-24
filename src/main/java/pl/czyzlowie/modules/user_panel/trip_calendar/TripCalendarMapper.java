package pl.czyzlowie.modules.user_panel.trip_calendar;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for TripCalendar entities and DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TripCalendarMapper {

    TripCalendarDto toDto(TripCalendar tripCalendar);

    TripCalendar toEntity(CreateTripCalendarRequest request);
}
