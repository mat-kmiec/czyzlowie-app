package pl.czyzlowie.modules.fish.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

/**
 * Represents a record of notable fish statistics in Poland.
 *
 * The PolishRecord class is designed to store information regarding
 * significant fishing records, including aspects such as weight, length,
 * and the year the record was set. It is primarily used as an embedded
 * component within other entities, enabling the association of fish records
 * with specific species or contexts.
 *
 * Fields in this class include:
 * - recordWeight: Represents the weight of the fish in the record.
 * - recordLength: Represents the length of the fish in the record.
 * - recordYear: Represents the year in which the record was achieved.
 *
 * This class is annotated with {@code @Embeddable}, making it suitable
 * for embedding within other entity classes.
 */
@Embeddable
@Data
public class PolishRecord {
    private Double recordWeight;
    private Double recordLength;
    private Integer recordYear;
}
