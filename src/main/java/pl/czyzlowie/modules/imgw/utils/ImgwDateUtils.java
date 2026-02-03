package pl.czyzlowie.modules.imgw.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class ImgwDateUtils {


    public static boolean isDateChanged(LocalDateTime oldDate, LocalDateTime newDate) {
        if (newDate == null) return false;
        if (oldDate == null) return true;
        return !oldDate.isEqual(newDate);
    }
}