package pl.czyzlowie.modules.map.entity;

public enum SpotType {
    LAKE("Jezioro", "jezioro"),
    RIVER("Rzeka", "rzeka"),
    OXBOW("Starorzecze", "starorzecze"),
    COMMERCIAL("Łowisko komercyjne", "lowisko-komercyjne"),
    SPECIFIC_SPOT("Miejscówka", "miejscowka"),
    SLIP("Slip", "miejsce-wodowania"),
    RESERVOIR("Zbiornik zaporowy", "zbiornik-zaporowy"),
    RESTRICTION("Zakaz", "zakaz");

    private final String displayName;
    private final String urlPath;

    SpotType(String displayName, String urlPath) {
        this.displayName = displayName;
        this.urlPath = urlPath;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public static SpotType fromUrlPath(String urlPath) {
        for (SpotType type : values()) {
            if (type.getUrlPath().equalsIgnoreCase(urlPath)) {
                return type;
            }
        }
        return null;
    }
}