package pl.czyzlowie.modules.map.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapMarkerDto {

    private String id;

    private String name;

    private BigDecimal lat;

    private BigDecimal lng;

    private String type;

    private String slug;


}
