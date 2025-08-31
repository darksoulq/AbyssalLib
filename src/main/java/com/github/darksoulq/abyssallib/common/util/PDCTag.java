package com.github.darksoulq.abyssallib.common.util;

import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

public class PDCTag {
    private final PersistentDataContainer container;

    public PDCTag(PersistentDataContainer container) {
        this.container = container;
    }

    public void set(Identifier key, String value) {
        container.set(key.toNamespace(), PersistentDataType.STRING, value);
    }

    public void set(Identifier key, int value) {
        container.set(key.toNamespace(), PersistentDataType.INTEGER, value);
    }

    public void set(Identifier key, int[] value) {
        container.set(key.toNamespace(), PersistentDataType.INTEGER_ARRAY, value);
    }

    public void set(Identifier key, boolean value) {
        container.set(key.toNamespace(), PersistentDataType.BYTE, (byte) (value ? 1 : 0));
    }

    public void set(Identifier key, float value) {
        container.set(key.toNamespace(), PersistentDataType.FLOAT, value);
    }

    public void set(Identifier key, byte value) {
        container.set(key.toNamespace(), PersistentDataType.BYTE, value);
    }

    public void set(Identifier key, byte[] value) {
        container.set(key.toNamespace(), PersistentDataType.BYTE_ARRAY, value);
    }

    public Optional<String> getString(Identifier key) {
        return Optional.ofNullable(container.get(key.toNamespace(), PersistentDataType.STRING));
    }

    public Optional<Integer> getInt(Identifier key) {
        return Optional.ofNullable(container.get(key.toNamespace(), PersistentDataType.INTEGER));
    }

    public Optional<int[]> getIntArray(Identifier key) {
        return Optional.ofNullable(container.get(key.toNamespace(), PersistentDataType.INTEGER_ARRAY));
    }

    public Optional<Boolean> getBoolean(Identifier key) {
        Byte val = container.get(key.toNamespace(), PersistentDataType.BYTE);
        return val == null ? Optional.empty() : Optional.of(val != 0);
    }

    public Optional<Float> getFloat(Identifier key) {
        return Optional.ofNullable(container.get(key.toNamespace(), PersistentDataType.FLOAT));
    }

    public Optional<Byte> getByte(Identifier key) {
        return Optional.ofNullable(container.get(key.toNamespace(), PersistentDataType.BYTE));
    }

    public Optional<byte[]> getByteArray(Identifier key) {
        return Optional.ofNullable(container.get(key.toNamespace(), PersistentDataType.BYTE_ARRAY));
    }

    public PersistentDataContainer toVanilla() {
        return container;
    }
}
