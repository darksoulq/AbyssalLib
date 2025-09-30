package com.github.darksoulq.abyssallib.common.util;

import net.minecraft.nbt.CompoundTag;

import java.util.Optional;

/**
 * A wrapper for {@link CompoundTag} that provides convenient
 * methods to store and retrieve common data types.
 */
public class CTag {
    /**
     * The underlying {@link CompoundTag}.
     */
    private final CompoundTag baseTag;

    /**
     * Constructs a new, empty {@code CTag}.
     */
    public CTag() {
        baseTag = new CompoundTag();
    }
    /**
     * Constructs a new {@code CTag} wrapping the given {@link CompoundTag}.
     *
     * @param base the tag to wrap
     */
    public CTag(CompoundTag base) {
        baseTag = base;
    }

    /**
     * Sets a {@link String} value for the given key.
     *
     * @param key   the key
     * @param value the value
     */
    public void set(String key, String value) {
        baseTag.putString(key, value);
    }
    /**
     * Sets an {@code int} value for the given key.
     *
     * @param key   the key
     * @param value the value
     */
    public void set(String key, int value) {
        baseTag.putInt(key, value);
    }
    /**
     * Sets an {@code int[]} value for the given key.
     *
     * @param key   the key
     * @param value the array value
     */
    public void set(String key, int[] value) {
        baseTag.putIntArray(key, value);
    }
    /**
     * Sets a {@code boolean} value for the given key.
     *
     * @param key   the key
     * @param value the value
     */
    public void set(String key, boolean value) {
        baseTag.putBoolean(key, value);
    }
    /**
     * Sets a {@code float} value for the given key.
     *
     * @param key   the key
     * @param value the value
     */
    public void set(String key, float value) {
        baseTag.putFloat(key, value);
    }
    /**
     * Sets a {@code byte} value for the given key.
     *
     * @param key   the key
     * @param value the value
     */
    public void set(String key, byte value) {
        baseTag.putByte(key, value);
    }
    /**
     * Sets a {@code byte[]} value for the given key.
     *
     * @param key   the key
     * @param value the array value
     */
    public void set(String key, byte[] value) {
        baseTag.putByteArray(key, value);
    }

    /**
     * Checks whether the given key exists in this tag.
     *
     * @param key the key
     * @return {@code true} if the key is present, otherwise {@code false}
     */
    public boolean has(String key) {
        return baseTag.contains(key);
    }

    /**
     * Gets the {@link String} value stored for the given key, if present.
     *
     * @param key the key
     * @return an {@link Optional} containing the value, or empty if not present
     */
    public Optional<String> getString(String key) {
        return baseTag.getString(key);
    }
    /**
     * Gets the {@code int} value stored for the given key, if present.
     *
     * @param key the key
     * @return an {@link Optional} containing the value, or empty if not present
     */
    public Optional<Integer> getInt(String key) {
        return baseTag.getInt(key);
    }
    /**
     * Gets the {@code int[]} value stored for the given key, if present.
     *
     * @param key the key
     * @return an {@link Optional} containing the array, or empty if not present
     */
    public Optional<int[]> getIntArray(String key) {
        return baseTag.getIntArray(key);
    }
    /**
     * Gets the {@code boolean} value stored for the given key, if present.
     *
     * @param key the key
     * @return an {@link Optional} containing the value, or empty if not present
     */
    public Optional<Boolean> getBoolean(String key) {
        return baseTag.getBoolean(key);
    }
    /**
     * Gets the {@code float} value stored for the given key, if present.
     *
     * @param key the key
     * @return an {@link Optional} containing the value, or empty if not present
     */
    public Optional<Float> getFloat(String key) {
        return baseTag.getFloat(key);
    }
    /**
     * Gets the {@code byte} value stored for the given key, if present.
     *
     * @param key the key
     * @return an {@link Optional} containing the value, or empty if not present
     */
    public Optional<Byte> getByte(String key) {
        return baseTag.getByte(key);
    }
    /**
     * Gets the {@code byte[]} value stored for the given key, if present.
     *
     * @param key the key
     * @return an {@link Optional} containing the array, or empty if not present
     */
    public Optional<byte[]> getByteArray(String key) {
        return baseTag.getByteArray(key);
    }

    /**
     * Returns the underlying {@link CompoundTag}.
     *
     * @return the wrapped tag
     */
    public CompoundTag toVanilla() {
        return baseTag;
    }
}
