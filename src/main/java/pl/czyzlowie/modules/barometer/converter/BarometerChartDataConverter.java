package pl.czyzlowie.modules.barometer.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import pl.czyzlowie.modules.barometer.dto.BarometerChartData;

/**
 * A converter class for transforming BarometerChartData objects into their JSON
 * string representation for database storage and back into Java objects upon retrieval.
 *
 * This class is annotated with {@code @Converter} and implements the {@code AttributeConverter}
 * interface from Jakarta Persistence API to handle conversions between
 * BarometerChartData and its serialized JSON format.
 *
 * The conversion process leverages the Jackson ObjectMapper for serializing and
 * deserializing the object, ensuring compatibility with JSON storage.
 *
 * Key functionalities of the class include:
 * - Converting BarometerChartData to JSON for storage in a database column.
 * - Parsing a stored JSON string back into a BarometerChartData object.
 *
 * Provides robust error handling by throwing {@code IllegalArgumentException}
 * in case of any issues during the data conversion process.
 */
@Converter
public class BarometerChartDataConverter implements AttributeConverter<BarometerChartData, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Converts a BarometerChartData object into its JSON string representation
     * for storing in a database column.
     *
     * @param attribute the BarometerChartData object to be converted. It can be null, in which case the method returns null.
     * @return a JSON string representing the barometer chart data, or null if the input is null.
     * @throws IllegalArgumentException if there is an error during the conversion process to JSON.
     */
    @Override
    public String convertToDatabaseColumn(BarometerChartData attribute) {
        if (attribute == null) return null;
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Błąd podczas konwersji danych wykresu do JSON", e);
        }
    }

    /**
     * Converts a database column value (JSON string) into a BarometerChartData entity.
     * This method parses the JSON representation of atmospheric pressure data and
     * maps it to a BarometerChartData object.
     *
     * @param dbData the JSON string representing the barometer chart data from the database.
     *               It can be null or blank, in which case null is returned.
     * @return the BarometerChartData object created by deserializing the JSON string,
     *         or null if the input JSON string is null or blank.
     * @throws IllegalArgumentException if an error occurs during the JSON parsing process.
     */
    @Override
    public BarometerChartData convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;
        try {
            return objectMapper.readValue(dbData, BarometerChartData.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Błąd podczas odczytu danych wykresu z JSON", e);
        }
    }
}
