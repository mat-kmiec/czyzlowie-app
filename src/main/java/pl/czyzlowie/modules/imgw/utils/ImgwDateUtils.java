package pl.czyzlowie.modules.imgw.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

/**
 * A utility class providing date-related operations for LocalDateTime objects.
 */
@UtilityClass
public class ImgwDateUtils {


    /**
     * Determines if two LocalDateTime values represent different dates.
     * If the newDate is null, the method returns false. If oldDate is null, the method returns true.
     * Otherwise, it compares the two dates for equality.
     *
     * @param oldDate the original date to compare, may be null
     * @param newDate the new date to compare, may be null
     * @return true if the dates are different or the oldDate is null; false otherwise
     */
    public static boolean isDateChanged(LocalDateTime oldDate, LocalDateTime newDate) {
        if (newDate == null) return false;
        if (oldDate == null) return true;
        return !oldDate.isEqual(newDate);
    }
}