package com.github.darksoulq.abyssallib.common.serialization.ops;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * An implementation of {@link DynamicOps} that serializes data into raw byte arrays.
 * <p>
 * This format uses a binary protocol where variable-length data (Strings, Lists, Maps)
 * is prefixed with a 4-byte integer indicating the length or size, followed by the raw data.
 * Fixed-size primitives use their standard IEEE 754 or two's complement binary representations.
 */
public class ByteOps extends DynamicOps<byte[]> {

    /** The singleton instance of ByteOps. */
    public static final ByteOps INSTANCE = new ByteOps();

    /** Private constructor to enforce the singleton pattern. */
    private ByteOps() {}

    /**
     * Serializes a string as UTF-8 bytes with a 4-byte length prefix.
     * @param value The string to serialize.
     * @return A byte array containing [length(4 bytes)][data(n bytes)].
     */
    @Override
    public byte[] createString(String value) {
        byte[] data = value.getBytes(StandardCharsets.UTF_8);
        return withLengthPrefix(data);
    }

    /**
     * Serializes an integer into a 4-byte array (Big-Endian).
     * @param value The integer value.
     * @return A 4-byte array.
     */
    @Override
    public byte[] createInt(int value) {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putInt(value);
        return buf.array();
    }

    /**
     * Serializes a long into an 8-byte array (Big-Endian).
     * @param value The long value.
     * @return An 8-byte array.
     */
    @Override
    public byte[] createLong(long value) {
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.putLong(value);
        return buf.array();
    }

    /**
     * Serializes a float into a 4-byte array.
     * @param value The float value.
     * @return A 4-byte array.
     */
    @Override
    public byte[] createFloat(float value) {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putFloat(value);
        return buf.array();
    }

    /**
     * Serializes a double into an 8-byte array.
     * @param value The double value.
     * @return An 8-byte array.
     */
    @Override
    public byte[] createDouble(double value) {
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.putDouble(value);
        return buf.array();
    }

    /**
     * Serializes a boolean into a 1-byte array.
     * @param value The boolean value.
     * @return An array containing {@code 1} for true or {@code 0} for false.
     */
    @Override
    public byte[] createBoolean(boolean value) {
        return new byte[]{ (byte)(value ? 1 : 0) };
    }

    /**
     * Serializes a list of byte arrays into a single contiguous byte array.
     * Format: [List Size(4)][Elem1 Length(4)][Elem1 Data...][Elem2 Length(4)][Elem2 Data...]
     * @param elements The list of serialized byte arrays.
     * @return The combined byte array.
     */
    @Override
    public byte[] createList(List<byte[]> elements) {
        int total = 4;
        for (byte[] elem : elements) total += 4 + elem.length;
        ByteBuffer buf = ByteBuffer.allocate(total);
        buf.putInt(elements.size());
        for (byte[] elem : elements) {
            buf.putInt(elem.length);
            buf.put(elem);
        }
        return buf.array();
    }

    /**
     * Serializes a map of byte arrays into a single contiguous byte array.
     * Format: [Map Size(4)][Key1 Len(4)][Key1 Data...][Val1 Len(4)][Val1 Data...]
     * @param map The map of serialized key-value pairs.
     * @return The combined byte array.
     */
    @Override
    public byte[] createMap(Map<byte[], byte[]> map) {
        int total = 4;
        for (Map.Entry<byte[], byte[]> e : map.entrySet()) {
            total += 4 + e.getKey().length;
            total += 4 + e.getValue().length;
        }
        ByteBuffer buf = ByteBuffer.allocate(total);
        buf.putInt(map.size());
        for (Map.Entry<byte[], byte[]> e : map.entrySet()) {
            buf.putInt(e.getKey().length).put(e.getKey());
            buf.putInt(e.getValue().length).put(e.getValue());
        }
        return buf.array();
    }

    /**
     * Decodes a length-prefixed byte array into a String.
     * @param input The raw byte data.
     * @return An Optional containing the decoded String, or empty if decoding fails.
     */
    @Override
    public Optional<String> getStringValue(byte[] input) {
        try {
            ByteBuffer buf = ByteBuffer.wrap(input);
            int len = buf.getInt();
            byte[] data = new byte[len];
            buf.get(data);
            return Optional.of(new String(data, StandardCharsets.UTF_8));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Decodes a 4-byte array into an Integer.
     * @param input The raw byte data.
     * @return An Optional containing the Integer.
     */
    @Override
    public Optional<Integer> getIntValue(byte[] input) {
        if (input.length != 4) return Optional.empty();
        return Optional.of(ByteBuffer.wrap(input).getInt());
    }

    /**
     * Decodes an 8-byte array into a Long.
     * @param input The raw byte data.
     * @return An Optional containing the Long.
     */
    @Override
    public Optional<Long> getLongValue(byte[] input) {
        if (input.length != 8) return Optional.empty();
        return Optional.of(ByteBuffer.wrap(input).getLong());
    }

    /**
     * Decodes a 4-byte array into a Float.
     * @param input The raw byte data.
     * @return An Optional containing the Float.
     */
    @Override
    public Optional<Float> getFloatValue(byte[] input) {
        if (input.length != 4) return Optional.empty();
        return Optional.of(ByteBuffer.wrap(input).getFloat());
    }

    /**
     * Decodes an 8-byte array into a Double.
     * @param input The raw byte data.
     * @return An Optional containing the Double.
     */
    @Override
    public Optional<Double> getDoubleValue(byte[] input) {
        if (input.length != 8) return Optional.empty();
        return Optional.of(ByteBuffer.wrap(input).getDouble());
    }

    /**
     * Decodes a 1-byte array into a Boolean.
     * @param input The raw byte data.
     * @return An Optional containing true if the byte is non-zero.
     */
    @Override
    public Optional<Boolean> getBooleanValue(byte[] input) {
        if (input.length != 1) return Optional.empty();
        return Optional.of(input[0] != 0);
    }

    /**
     * Decodes a combined byte array into a list of its constituent byte array elements.
     * @param input The raw binary list data.
     * @return An Optional containing the list of byte arrays.
     */
    @Override
    public Optional<List<byte[]>> getList(byte[] input) {
        try {
            ByteBuffer buf = ByteBuffer.wrap(input);
            int size = buf.getInt();
            List<byte[]> result = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                int len = buf.getInt();
                byte[] elem = new byte[len];
                buf.get(elem);
                result.add(elem);
            }
            return Optional.of(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Decodes a combined byte array into a map of its constituent key-value byte arrays.
     * @param input The raw binary map data.
     * @return An Optional containing the map.
     */
    @Override
    public Optional<Map<byte[], byte[]>> getMap(byte[] input) {
        try {
            ByteBuffer buf = ByteBuffer.wrap(input);
            int size = buf.getInt();
            Map<byte[], byte[]> result = new LinkedHashMap<>();
            for (int i = 0; i < size; i++) {
                int klen = buf.getInt();
                byte[] key = new byte[klen];
                buf.get(key);
                int vlen = buf.getInt();
                byte[] val = new byte[vlen];
                buf.get(val);
                result.put(key, val);
            }
            return Optional.of(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * @return An empty 0-length byte array.
     */
    @Override
    public byte[] empty() {
        return new byte[0];
    }

    /**
     * Helper method to wrap data with a 4-byte length header.
     * @param data The payload.
     * @return A new array containing [length][payload].
     */
    private byte[] withLengthPrefix(byte[] data) {
        ByteBuffer buf = ByteBuffer.allocate(4 + data.length);
        buf.putInt(data.length).put(data);
        return buf.array();
    }
}