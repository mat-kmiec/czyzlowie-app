package pl.czyzlowie.modules.fish.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class PolishRecord {
    private Double recordWeight;
    private Double recordLength;
    private Integer recordYear;
}
