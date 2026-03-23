package pl.czyzlowie.modules.map.entity;

/**
 * Defines the types of restrictions that can be applied within the context of map-related entities.
 *
 * This enumeration is used to specify different levels of access or activities allowed at certain
 * locations, such as a lake, river, or boat slip. The restrictions can impact user permissions or
 * actions based on the specified type.
 *
 * Enum Constants:
 * - `TOTAL_BAN`: Represents a full prohibition, where no access or specific activity is allowed.
 * - `PARTIAL_LIMIT`: Represents a restrictive condition where access or activities are limited
 *   or regulated under certain conditions.
 *
 * Typical usage includes specifying the restrictions applied to geographical entities such as
 * lakes, reservoirs, or other regions.
 */
public enum RestrictionType {
    TOTAL_BAN,
    PARTIAL_LIMIT
}
