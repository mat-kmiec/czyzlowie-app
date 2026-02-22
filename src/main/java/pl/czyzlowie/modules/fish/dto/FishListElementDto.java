package pl.czyzlowie.modules.fish.dto;

import lombok.Builder;
import lombok.Data;
import pl.czyzlowie.modules.fish.entity.enums.FishCategory;

@Data
@Builder
public class FishListElementDto {
    private Long id;
    private String name;
    private String latinName;
    private String slug;
    private String imgUrl;
    private FishCategory category;
}
