package com.github.darksoulq.abyssallib.world.data.attribute;

import net.kyori.adventure.key.Key;

/**
 * Represents a definition for a custom numeric attribute.
 * Attributes define a base system for calculating dynamic entity stats.
 */
public final class Attribute {
    /** The unique key identifying this attribute definition. */
    private final Key key;
    /** The default base value used if no data is stored for this attribute. */
    private final double defaultValue;

    /**
     * Constructs a new attribute definition.
     *
     * @param key          The unique key for this attribute.
     * @param defaultValue The fallback base value when not explicitly set.
     */
    public Attribute(Key key, double defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the unique key used to identify this attribute.
     *
     * @return The attribute key.
     */
    public Key key() {
        return key;
    }

    /**
     * Returns the default base value of this attribute.
     *
     * @return The default value.
     */
    public double defaultValue() {
        return defaultValue;
    }
}