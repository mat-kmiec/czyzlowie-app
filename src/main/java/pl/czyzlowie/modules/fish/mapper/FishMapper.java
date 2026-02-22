package pl.czyzlowie.modules.fish.mapper;

import org.springframework.stereotype.Component;
import pl.czyzlowie.modules.fish.dto.FishListElementDto;
import pl.czyzlowie.modules.fish.entity.FishSpecies;

@Component
public class FishMapper {

    public FishListElementDto toListDto(FishSpecies fish) {
        if (fish == null) return null;

        return FishListElementDto.builder()
                .id(fish.getId())
                .name(fish.getName())
                .latinName(fish.getLatinName())
                .slug(fish.getSlug())
                .imgUrl(fish.getImgUrl())
                .category(fish.getCategory())
                .build();
    }
}