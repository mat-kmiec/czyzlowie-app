package pl.czyzlowie.modules.moon.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import pl.czyzlowie.modules.moon.client.dto.AstronomyResponse;
import pl.czyzlowie.modules.moon.entity.MoonData;
import pl.czyzlowie.modules.moon.entity.MoonRegion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MoonDataMapper {

    default MoonData toEntity(AstronomyResponse dto, MoonRegion region, LocalDate date) {
        if (dto == null || dto.getData() == null || dto.getData().getMoon() == null || dto.getData().getMoon().isEmpty()) {
            return null;
        }

        var moon = dto.getData().getMoon().get(0);
        var builder = MoonData.builder()
                .date(date)
                .regionNode(region)
                .fetchedAt(LocalDateTime.now());

        if (moon.getPhase() != null) {
            builder.phaseName(moon.getPhase().getName())
                    .illumination(BigDecimal.valueOf(moon.getPhase().getIllumination()))
                    .moonAgeDays(BigDecimal.valueOf(moon.getPhase().getAge()));
        }

        if (moon.getDistance() != null) {
            builder.distanceKm(BigDecimal.valueOf(moon.getDistance().getKm()));
        }

        if (moon.getEvents() != null) {
            for (var event : moon.getEvents()) {
                LocalDateTime time = parseIso(event.getTime());
                switch (event.getType()) {
                    case "rise" -> builder.moonrise(time);
                    case "set" -> builder.moonset(time);
                    case "meridian", "transit" -> builder.majorPeriodStart(time);
                }
            }
        }
        return builder.build();
    }

    private LocalDateTime parseIso(String iso) {
        return iso != null ? LocalDateTime.parse(iso, DateTimeFormatter.ISO_DATE_TIME) : null;
    }
}