package com.github.darksoulq.abyssallib.world.util;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

/**
 * A wrapper for Bukkit's {@link PersistentDataContainer} (PDC) API.
 * <p>
 * This class simplifies data persistence on Bukkit objects (like ItemMeta, Entities, and TileEntities)
 * by abstracting {@link PersistentDataType} handling and utilizing {@link NamespacedKey}
 * for key management.
 */
public class PDCTag {
    /** The underlying Bukkit data container being wrapped. */
    private final PersistentDataContainer container;

    /**
     * Constructs a new PDCTag wrapper for a specific container.
     *
     * @param container The {@link PersistentDataContainer} to wrap.
     */
    public PDCTag(PersistentDataContainer container) {
        this.container = container;
    }

    /**
     * Stores a String value in the container.
     *
     * @param key   The unique {@link NamespacedKey} key.
     * @param value The string value to store.
     */
    public void set(NamespacedKey key, String value) {
        container.set(key, PersistentDataType.STRING, value);
    }

    /**
     * Stores an integer value in the container.
     *
     * @param key   The unique {@link NamespacedKey} key.
     * @param value The integer value to store.
     */
    public void set(NamespacedKey key, int value) {
        container.set(key, PersistentDataType.INTEGER, value);
    }

    /**
     * Stores an integer array in the container.
     *
     * @param key   The unique {@link NamespacedKey} key.
     * @param value The integer array to store.
     */
    public void set(NamespacedKey key, int[] value) {
        container.set(key, PersistentDataType.INTEGER_ARRAY, value);
    }

    /**
     * Stores a boolean value in the container.
     *
     * @param key   The unique {@link NamespacedKey} key.
     * @param value The boolean value to store.
     */
    public void set(NamespacedKey key, boolean value) {
        container.set(key, PersistentDataType.BOOLEAN, value);
    }

    /**
     * Stores a float value in the container.
     *
     * @param key   The unique {@link NamespacedKey} key.
     * @param value The float value to store.
     */
    public void set(NamespacedKey key, float value) {
        container.set(key, PersistentDataType.FLOAT, value);
    }

    /**
     * Stores a byte value in the container.
     *
     * @param key   The unique {@link NamespacedKey} key.
     * @param value The byte value to store.
     */
    public void set(NamespacedKey key, byte value) {
        container.set(key, PersistentDataType.BYTE, value);
    }

    /**
     * Stores a byte array in the container.
     *
     * @param key   The unique {@link NamespacedKey} key.
     * @param value The byte array to store.
     */
    public void set(NamespacedKey key, byte[] value) {
        container.set(key, PersistentDataType.BYTE_ARRAY, value);
    }

    /**
     * Retrieves a String value from the container.
     *
     * @param key The unique {@link NamespacedKey} key.
     * @return An {@link Optional} containing the string value if present.
     */
    public Optional<String> getString(NamespacedKey key) {
        return Optional.ofNullable(container.get(key, PersistentDataType.STRING));
    }

    /**
     * Retrieves an Integer value from the container.
     *
     * @param key The unique {@link NamespacedKey} key.
     * @return An {@link Optional} containing the integer value if present.
     */
    public Optional<Integer> getInt(NamespacedKey key) {
        return Optional.ofNullable(container.get(key, PersistentDataType.INTEGER));
    }

    /**
     * Retrieves an Integer array from the container.
     *
     * @param key The unique {@link NamespacedKey} key.
     * @return An {@link Optional} containing the integer array if present.
     */
    public Optional<int[]> getIntArray(NamespacedKey key) {
        return Optional.ofNullable(container.get(key, PersistentDataType.INTEGER_ARRAY));
    }

    /**
     * Retrieves a Boolean value from the container.
     *
     * @param key The unique {@link NamespacedKey} key.
     * @return An {@link Optional} containing the boolean value if present.
     */
    public Optional<Boolean> getBoolean(NamespacedKey key) {
        return Optional.ofNullable(container.get(key, PersistentDataType.BOOLEAN));
    }

    /**
     * Retrieves a Float value from the container.
     *
     * @param key The unique {@link NamespacedKey} key.
     * @return An {@link Optional} containing the float value if present.
     */
    public Optional<Float> getFloat(NamespacedKey key) {
        return Optional.ofNullable(container.get(key, PersistentDataType.FLOAT));
    }

    /**
     * Retrieves a Byte value from the container.
     *
     * @param key The unique {@link NamespacedKey} key.
     * @return An {@link Optional} containing the byte value if present.
     */
    public Optional<Byte> getByte(NamespacedKey key) {
        return Optional.ofNullable(container.get(key, PersistentDataType.BYTE));
    }

    /**
     * Retrieves a Byte array from the container.
     *
     * @param key The unique {@link NamespacedKey} key.
     * @return An {@link Optional} containing the byte array if present.
     */
    public Optional<byte[]> getByteArray(NamespacedKey key) {
        return Optional.ofNullable(container.get(key, PersistentDataType.BYTE_ARRAY));
    }

    /**
     * Returns the raw Bukkit container.
     *
     * @return The underlying {@link PersistentDataContainer}.
     */
    public PersistentDataContainer toVanilla() {
        return container;
    }
}