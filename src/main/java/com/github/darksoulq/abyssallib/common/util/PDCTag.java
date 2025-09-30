package com.github.darksoulq.abyssallib.common.util;

import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

/**
 * A wrapper for {@link PersistentDataContainer} that provides convenient
 * methods to store and retrieve common data types.
 */
public class PDCTag {
    /**
     * The underlying {@link PersistentDataContainer}.
     */
    private final PersistentDataContainer container;

    /**
     * Constructs a new {@code PDCTag} wrapping the given {@link PersistentDataContainer}.
     *
     * @param container the container to wrap
     */
    public PDCTag(PersistentDataContainer container) {
        this.container = container;
    }

    /**
     * Sets a {@link String} value for the given key.
     *
     * @param key   the key
     * @param value the value
     */
    public void set(Identifier key, String value) {
        container.set(key.asNamespacedKey(), PersistentDataType.STRING, value);
    }

    /**
     * Sets an {@code int} value for the given key.
     *
     * @param key   the key
     * @param value the value
     */
    public void set(Identifier key, int value) {
        container.set(key.asNamespacedKey(), PersistentDataType.INTEGER, value);
    }

    /**
     * Sets an {@code int[]} value for the given key.
     *
     * @param key   the key
     * @param value the array value
     */
    public void set(Identifier key, int[] value) {
        container.set(key.asNamespacedKey(), PersistentDataType.INTEGER_ARRAY, value);
    }

    /**
     * Sets a {@code boolean} value for the given key.
     *
     * @param key   the key
     * @param value the value
     */
    public void set(Identifier key, boolean value) {
        container.set(key.asNamespacedKey(), PersistentDataType.BOOLEAN, value);
    }

    /**
     * Sets a {@code float} value for the given key.
     *
     * @param key   the key
     * @param value the value
     */
    public void set(Identifier key, float value) {
        container.set(key.asNamespacedKey(), PersistentDataType.FLOAT, value);
    }

    /**
     * Sets a {@code byte} value for the given key.
     *
     * @param key   the key
     * @param value the value
     */
    public void set(Identifier key, byte value) {
        container.set(key.asNamespacedKey(), PersistentDataType.BYTE, value);
    }

    /**
     * Sets a {@code byte[]} value for the given key.
     *
     * @param key   the key
     * @param value the array value
     */
    public void set(Identifier key, byte[] value) {
        container.set(key.asNamespacedKey(), PersistentDataType.BYTE_ARRAY, value);
    }

    /**
     * Gets the {@link String} value stored for the given key, if present.
     *
     * @param key the key
     * @return an {@link Optional} containing the value, or empty if not present
     */
    public Optional<String> getString(Identifier key) {
        return Optional.ofNullable(container.get(key.asNamespacedKey(), PersistentDataType.STRING));
    }

    /**
     * Gets the {@code int} value stored for the given key, if present.
     *
     * @param key the key
     * @return an {@link Optional} containing the value, or empty if not present
     */
    public Optional<Integer> getInt(Identifier key) {
        return Optional.ofNullable(container.get(key.asNamespacedKey(), PersistentDataType.INTEGER));
    }

    /**
     * Gets the {@code int[]} value stored for the given key, if present.
     *
     * @param key the key
     * @return an {@link Optional} containing the array, or empty if not present
     */
    public Optional<int[]> getIntArray(Identifier key) {
        return Optional.ofNullable(container.get(key.asNamespacedKey(), PersistentDataType.INTEGER_ARRAY));
    }

    /**
     * Gets the {@code boolean} value stored for the given key, if present.
     *
     * @param key the key
     * @return an {@link Optional} containing the value, or empty if not present
     */
    public Optional<Boolean> getBoolean(Identifier key) {
        return Optional.ofNullable(container.get(key.asNamespacedKey(), PersistentDataType.BOOLEAN));
    }

    /**
     * Gets the {@code float} value stored for the given key, if present.
     *
     * @param key the key
     * @return an {@link Optional} containing the value, or empty if not present
     */
    public Optional<Float> getFloat(Identifier key) {
        return Optional.ofNullable(container.get(key.asNamespacedKey(), PersistentDataType.FLOAT));
    }

    /**
     * Gets the {@code byte} value stored for the given key, if present.
     *
     * @param key the key
     * @return an {@link Optional} containing the value, or empty if not present
     */
    public Optional<Byte> getByte(Identifier key) {
        return Optional.ofNullable(container.get(key.asNamespacedKey(), PersistentDataType.BYTE));
    }

    /**
     * Gets the {@code byte[]} value stored for the given key, if present.
     *
     * @param key the key
     * @return an {@link Optional} containing the array, or empty if not present
     */
    public Optional<byte[]> getByteArray(Identifier key) {
        return Optional.ofNullable(container.get(key.asNamespacedKey(), PersistentDataType.BYTE_ARRAY));
    }

    /**
     * Returns the underlying {@link PersistentDataContainer}.
     *
     * @return the wrapped container
     */
    public PersistentDataContainer toVanilla() {
        return container;
    }
}
