package com.github.darksoulq.abyssallib.block;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * A class representing data for a block, which can store various types of values
 * in a {@link JsonObject}.
 */
public class BlockData {
    /**
     * The internal JSON object storing the block's key-value data.
     */
    private final JsonObject data = new JsonObject();
    /**
     * A callback that is triggered when the data is modified.
     */
    private Runnable saveCallback;

    /**
     * Sets a callback to be invoked when the block data is modified. (this shouldn't be set by user)
     *
     * @param saveCallback The {@link Runnable} callback to run when the data is marked as dirty.
     */
    public void setSaveCallback(Runnable saveCallback) {
        this.saveCallback = saveCallback;
    }

    /**
     * Marks the block data as dirty (changed), triggering the save callback if it is set.
     */
    private void markDirty() {
        if (saveCallback != null) saveCallback.run();
    }

    /**
     * Sets a value of any {@link JsonElement} type in the block data.
     *
     * @param key   The key under which to store the value.
     * @param value The {@link JsonElement} value to store.
     */
    public void set(String key, JsonElement value) {
        data.add(key, value);
        markDirty();
    }

    /**
     * Sets a string value in the block data.
     *
     * @param key   The key under which to store the value.
     * @param value The string value to store.
     */
    public void setString(String key, String value) {
        data.addProperty(key, value);
        markDirty();
    }

    /**
     * Sets an integer value in the block data.
     *
     * @param key   The key under which to store the value.
     * @param value The integer value to store.
     */
    public void setInt(String key, int value) {
        data.addProperty(key, value);
        markDirty();
    }

    /**
     * Sets a boolean value in the block data.
     *
     * @param key   The key under which to store the value.
     * @param value The boolean value to store.
     */
    public void setBoolean(String key, boolean value) {
        data.addProperty(key, value);
        markDirty();
    }

    /**
     * Sets a double value in the block data.
     *
     * @param key   The key under which to store the value.
     * @param value The double value to store.
     */
    public void setDouble(String key, double value) {
        data.addProperty(key, value);
        markDirty();
    }

    /**
     * Retrieves the JsonElement value associated with the specified key.
     *
     * @param key The key whose associated value is to be returned.
     * @return The JsonElement value associated with the key, or {@code null} if not found.
     */
    public JsonElement get(String key) {
        return data.has(key) ? data.get(key) : null;
    }

    /**
     * Retrieves the string value associated with the specified key.
     *
     * @param key The key whose associated value is to be returned.
     * @return The string value associated with the key, or {@code null} if not found.
     */
    public String getString(String key) {
        return data.has(key) ? data.get(key).getAsString() : null;
    }

    /**
     * Retrieves the integer value associated with the specified key.
     *
     * @param key The key whose associated value is to be returned.
     * @return The integer value associated with the key, or {@code 0} if not found.
     */
    public int getInt(String key) {
        return data.has(key) ? data.get(key).getAsInt() : 0;
    }

    /**
     * Retrieves the boolean value associated with the specified key.
     *
     * @param key The key whose associated value is to be returned.
     * @return The boolean value associated with the key, or {@code false} if not found.
     */
    public boolean getBoolean(String key) {
        return data.has(key) && data.get(key).getAsBoolean();
    }

    /**
     * Retrieves the double value associated with the specified key.
     *
     * @param key The key whose associated value is to be returned.
     * @return The double value associated with the key, or {@code 0.0} if not found.
     */
    public double getDouble(String key) {
        return data.has(key) ? data.get(key).getAsDouble() : 0.0;
    }

    /**
     * Retrieves the raw {@link JsonObject} that contains all the data.
     *
     * @return The raw {@link JsonObject} containing the block data.
     */
    public JsonObject getRaw() {
        return data;
    }

    /**
     * Creates a {@link BlockData} object from the provided JSON object.
     *
     * @param json The {@link JsonObject} containing the block data.
     * @return A new {@link BlockData} instance populated with the data from the provided JSON.
     */
    public static BlockData fromJson(JsonObject json) {
        BlockData blockData = new BlockData();
        json.entrySet().forEach(entry -> blockData.data.add(entry.getKey(), entry.getValue()));
        return blockData;
    }
}
