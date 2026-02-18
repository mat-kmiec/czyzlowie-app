package pl.czyzlowie.modules.moon.projection;

import java.math.BigDecimal;

public interface StationCoordinatesView {
    String getId();
    BigDecimal getLatitude();
    BigDecimal getLongitude();
}
