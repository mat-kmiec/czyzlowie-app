package pl.czyzlowie.modules.fish.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import pl.czyzlowie.modules.fish.entity.enums.ActivityLevel;

@Embeddable
@Data
public class ActivityCalendar {
    @Enumerated(EnumType.STRING) private ActivityLevel janActivity;
    @Enumerated(EnumType.STRING) private ActivityLevel febActivity;
    @Enumerated(EnumType.STRING) private ActivityLevel marActivity;
    @Enumerated(EnumType.STRING) private ActivityLevel aprActivity;
    @Enumerated(EnumType.STRING) private ActivityLevel mayActivity;
    @Enumerated(EnumType.STRING) private ActivityLevel junActivity;
    @Enumerated(EnumType.STRING) private ActivityLevel julActivity;
    @Enumerated(EnumType.STRING) private ActivityLevel augActivity;
    @Enumerated(EnumType.STRING) private ActivityLevel sepActivity;
    @Enumerated(EnumType.STRING) private ActivityLevel octActivity;
    @Enumerated(EnumType.STRING) private ActivityLevel novActivity;
    @Enumerated(EnumType.STRING) private ActivityLevel decActivity;
}