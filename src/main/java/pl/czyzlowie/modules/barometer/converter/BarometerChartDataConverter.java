package pl.czyzlowie.modules.barometer.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import pl.czyzlowie.modules.barometer.dto.BarometerChartData;

@Converter
public class BarometerChartDataConverter implements AttributeConverter<BarometerChartData, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(BarometerChartData attribute) {
        if (attribute == null) return null;
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Błąd podczas konwersji danych wykresu do JSON", e);
        }
    }

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
