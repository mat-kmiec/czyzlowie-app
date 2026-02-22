package pl.czyzlowie.modules.fish.dto;

import lombok.Data;
import pl.czyzlowie.modules.fish.entity.enums.FishCategory;

@Data
public class FishFilterDto {
    private String name;
    private FishCategory category;
}
