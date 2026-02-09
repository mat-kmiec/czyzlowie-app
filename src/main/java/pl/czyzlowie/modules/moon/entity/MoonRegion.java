package pl.czyzlowie.modules.moon.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MoonRegion {
    NW(53.42, 14.55), // Szczecin
    N(54.35, 18.64),  // Gdańsk
    NE(54.10, 22.93), // Suwałki
    W(52.40, 16.92),  // Poznań
    C(51.75, 19.45),  // Łódź
    E(51.24, 22.56),  // Lublin
    SW(51.10, 17.03), // Wrocław
    S(50.06, 19.94),  // Kraków
    SE(50.04, 22.00); // Rzeszów

    private final double latitude;
    private final double longitude;

    public static MoonRegion findNearest(double lat, double lon) {
        MoonRegion nearest = C;
        double minDist = Double.MAX_VALUE;
        for (MoonRegion region : values()) {
            double dist = Math.sqrt(Math.pow(region.latitude - lat, 2) + Math.pow(region.longitude - lon, 2));
            if (dist < minDist) {
                minDist = dist;
                nearest = region;
            }
        }
        return nearest;
    }
}