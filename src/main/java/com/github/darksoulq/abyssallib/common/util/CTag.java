package com.github.darksoulq.abyssallib.common.util;

import net.minecraft.nbt.CompoundTag;

import java.util.Optional;

public class CTag {
    private final CompoundTag baseTag;

    public CTag() {
        baseTag = new CompoundTag();
    }
    public CTag(CompoundTag base) {
        baseTag = base;
    }

    public void set(String key, String value) {
        baseTag.putString(key, value);
    }
    public void set(String key, int value) {
        baseTag.putInt(key, value);
    }
    public void set(String key, int[] value) {
        baseTag.putIntArray(key, value);
    }
    public void set(String key, boolean value) {
        baseTag.putBoolean(key, value);
    }
    public void set(String key, float value) {
        baseTag.putFloat(key, value);
    }
    public void set(String key, byte value) {
        baseTag.putByte(key, value);
    }
    public void set(String key, byte[] value) {
        baseTag.putByteArray(key, value);
    }

    public boolean has(String key) {
        return baseTag.contains(key);
    }

    public Optional<String> getString(String key) {
        return baseTag.getString(key);
    }
    public Optional<Integer> getInt(String key) {
        return baseTag.getInt(key);
    }
    public Optional<int[]> getIntArray(String key) {
        return baseTag.getIntArray(key);
    }
    public Optional<Boolean> getBoolean(String key) {
        return baseTag.getBoolean(key);
    }
    public Optional<Float> getFloat(String key) {
        return baseTag.getFloat(key);
    }
    public Optional<Byte> getByte(String key) {
        return baseTag.getByte(key);
    }
    public Optional<byte[]> getByteArray(String key) {
        return baseTag.getByteArray(key);
    }

    public CompoundTag toVanilla() {
        return baseTag;
    }
}
