package pl.czyzlowie.modules.map.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapMarkerDto {

    private String id;
    private String name;
    private Double lat;
    private Double lng;
    private String type;
    private String slug;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String restrictionType;
    private String polygonCoordinates;
}
