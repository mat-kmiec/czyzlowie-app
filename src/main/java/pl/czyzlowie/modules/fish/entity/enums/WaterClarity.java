package pl.czyzlowie.modules.fish.entity.enums;

/**
 * Defines the transparency or turbidity levels of the water and their influence on fish visibility and behavior.
 *
 * This enum is used to categorize how well light penetrates the water column, which affects
 * how specific fish species hunt or hide. It helps the forecasting algorithm determine
 * the effectiveness of certain lures and the overall activity of the species in different
 * environmental conditions.
 *
 * Values:
 * - CLEAR: High transparency, typically found in mountain streams or oligotrophic lakes.
 * - NORMAL: Standard visibility levels for most inland water bodies.
 * - MUDDY: High turbidity or "cafe au lait" water, often caused by heavy rain, floods, or algae blooms.
 * - ANY: Indicates that the species' feeding habits are not significantly affected by water clarity.
 */
public enum WaterClarity {
    CLEAR,
    NORMAL,
    MUDDY,
    ANY
}
