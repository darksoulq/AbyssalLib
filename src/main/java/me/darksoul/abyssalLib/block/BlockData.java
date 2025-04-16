package me.darksoul.abyssalLib.block;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class BlockData {
    private final JsonObject data = new JsonObject();
    private Runnable saveCallback;

    public void setSaveCallback(Runnable saveCallback) {
        this.saveCallback = saveCallback;
    }

    private void markDirty() {
        if (saveCallback != null) saveCallback.run();
    }

    public void set(String key, JsonElement value) {
        data.add(key, value);
        markDirty();
    }

    public void setString(String key, String value) {
        data.addProperty(key, value);
        markDirty();
    }

    public void setInt(String key, int value) {
        data.addProperty(key, value);
        markDirty();
    }

    public void setBoolean(String key, boolean value) {
        data.addProperty(key, value);
        markDirty();
    }

    public void setDouble(String key, double value) {
        data.addProperty(key, value);
        markDirty();
    }

    public String getString(String key) {
        return data.has(key) ? data.get(key).getAsString() : null;
    }

    public int getInt(String key) {
        return data.has(key) ? data.get(key).getAsInt() : 0;
    }

    public boolean getBoolean(String key) {
        return data.has(key) && data.get(key).getAsBoolean();
    }

    public double getDouble(String key) {
        return data.has(key) ? data.get(key).getAsDouble() : 0.0;
    }

    public JsonObject getRaw() {
        return data;
    }

    public static BlockData fromJson(JsonObject json) {
        BlockData blockData = new BlockData();
        json.entrySet().forEach(entry -> blockData.data.add(entry.getKey(), entry.getValue()));
        return blockData;
    }
}
