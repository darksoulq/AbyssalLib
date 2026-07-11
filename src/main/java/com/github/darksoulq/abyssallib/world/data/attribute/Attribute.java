package com.github.darksoulq.abyssallib.world.data.attribute;

import net.kyori.adventure.key.Key;

/**
 * Represents a definition for a custom numeric attribute.
 * Attributes define a base system for calculating dynamic entity stats.
 *
 * @param key          The unique key identifying this attribute definition.
 * @param defaultValue The default base value used if no data is stored for this attribute.
 */
public record Attribute(Key key, double defaultValue) {
    /**
     * Constructs a new attribute definition.
     *
     * @param key          The unique key for this attribute.
     * @param defaultValue The fallback base value when not explicitly set.
     */
    public Attribute {
    }

    /**
     * Returns the unique key used to identify this attribute.
     *
     * @return The attribute key.
     */
    @Override
    public Key key() {
        return key;
    }

    /**
     * Returns the default base value of this attribute.
     *
     * @return The default value.
     */
    @Override
    public double defaultValue() {
        return defaultValue;
    }
}