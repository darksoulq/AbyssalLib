package com.github.darksoulq.abyssallib.world.level.data.player;

/**
 * Defines arithmetic operations that can be applied to numeric attributes via {@link AttributeModifier}.
 * <p>
 * These operations are applied in the context of numeric types such as {@code Integer}, {@code Float},
 * {@code Double}, {@code Long}, {@code Short}, {@code Byte}, and {@code BigDecimal}.
 */
public enum AttributeOperation {

    /**
     * Adds the modifier to the base value.
     * <p>Example: {@code base + modifier}</p>
     */
    ADD,

    /**
     * Subtracts the modifier from the base value.
     * <p>Example: {@code base - modifier}</p>
     */
    SUBTRACT,

    /**
     * Multiplies the base value by the modifier.
     * <p>Example: {@code base * modifier}</p>
     */
    MULTIPLY,

    /**
     * Divides the base value by the modifier.
     * If the modifier is zero, division is skipped and the base value is returned unchanged.
     * <p>Example: {@code base / modifier}</p>
     */
    DIVIDE
}
