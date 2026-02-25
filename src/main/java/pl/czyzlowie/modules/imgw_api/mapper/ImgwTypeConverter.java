package pl.czyzlowie.modules.imgw_api.mapper;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * The {@code ImgwTypeConverter} class provides utility methods for type conversion.
 * It is a Spring-managed component and is typically used as a mapping utility in conjunction
 * with MapStruct-based mappers. This class offers methods for parsing various types such as
 * {@code BigDecimal}, {@code Integer}, {@code LocalDateTime}, and {@code LocalDate} from
 * their string representations.
 *
 * The class uses predefined {@code DateTimeFormatter} instances for date and time parsing
 * and accounts for invalid inputs by returning {@code null} for unsupported formats or
 * unexpected values.
 *
 * This class is commonly utilized in other components of the project to handle data format
 * transformations, such as converting API response data into entity objects.
 */
@Component
public class ImgwTypeConverter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Parses a string value into a {@code BigDecimal}.
     * If the input is {@code null}, empty, or cannot be parsed as a valid decimal number,
     * the method returns {@code null}. The input is trimmed, and commas are replaced with
     * periods for international compatibility.
     *
     * @param value the string representation of a decimal number to be parsed
     * @return the parsed {@code BigDecimal}, or {@code null} if the input is invalid
     */
    @Named("parseDecimal")
    public BigDecimal parseDecimal(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return new BigDecimal(value.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parses a string value into an {@code Integer}.
     * If the input is {@code null}, empty, or cannot be parsed as an integer, the method returns {@code null}.
     * The method handles both plain integers and decimal numbers. For decimal inputs,
     * the integer portion is extracted using {@code BigDecimal}.
     *
     * @param value the string representation of a number to be parsed
     * @return the parsed {@code Integer}, or {@code null} if the input is invalid
     */
    @Named("parseInteger")
    public Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            if (value.contains(".")) {
                return new BigDecimal(value.trim().replace(",", ".")).intValue();
            }
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parses a date and time string into a {@code LocalDateTime} object using a specific formatter.
     * If the input string is null, empty, or cannot be parsed, the method returns {@code null}.
     *
     * @param value the string representation of the date and time to be parsed
     * @return the corresponding {@code LocalDateTime} object, or {@code null} if the input is invalid
     */
    @Named("parseDateTime")
    public LocalDateTime parseDateTime(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return LocalDateTime.parse(value.trim(), DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parses a string value into a {@code LocalDate} object using a predefined date formatter.
     * If the input string is null, empty, or cannot be parsed, the method returns {@code null}.
     *
     * @param value the string representation of the date to be parsed
     * @return a {@code LocalDate} object representing the parsed date, or {@code null} if the input is invalid
     */
    @Named("parseDate")
    public LocalDate parseDate(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(value.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}