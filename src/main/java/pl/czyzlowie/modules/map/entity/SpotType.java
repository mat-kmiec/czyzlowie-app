package pl.czyzlowie.modules.map.entity;

/**
 * Represents different types of spots or locations for mapping or categorization purposes.
 * This enumeration defines specific spot types along with associated metadata such as
 * display names and URL path identifiers.
 *
 * Spot types include:
 * - LAKE: Represents a lake entity, with a display name "Jezioro".
 * - RIVER: Represents a river entity, with a display name "Rzeka".
 * - OXBOW: Represents an oxbow lake entity, with a display name "Starorzecze".
 * - COMMERCIAL: Represents a commercial fishing spot, with a display name "Łowisko komercyjne".
 * - SPECIFIC_SPOT: Represents a general or specific location, with a display name "Miejscówka".
 * - SLIP: Represents a boat slip or place for boat launching, with a display name "Slip".
 * - RESERVOIR: Represents a dam reservoir, with a display name "Zbiornik zaporowy".
 * - RESTRICTION: Represents a restricted area, with a display name "Zakaz".
 *
 * Each type has:
 * - A display name in the local language.
 * - A URL-friendly path segment used for identification in web addressing.
 *
 * Provides utility methods for retrieving:
 * - The display name associated with a spot type.
 * - The URL path associated with a spot type.
 * - A spot type from a URL path string.
 */
public enum SpotType {
    LAKE("Jezioro", "jezioro"),
    RIVER("Rzeka", "rzeka"),
    OXBOW("Starorzecze", "starorzecze"),
    COMMERCIAL("Łowisko komercyjne", "lowisko-komercyjne"),
    SPECIFIC_SPOT("Miejscówka", "miejscowka"),
    SLIP("Slip", "miejsce-wodowania"),
    RESERVOIR("Zbiornik zaporowy", "zbiornik-zaporowy"),
    RESTRICTION("Zakaz", "zakaz"),
    FISHING_SHOP("Sklep wędkarski", "sklep-wedkarski");

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