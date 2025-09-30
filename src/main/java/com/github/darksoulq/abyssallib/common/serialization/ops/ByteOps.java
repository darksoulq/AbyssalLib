package com.github.darksoulq.abyssallib.common.serialization.ops;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * {@link DynamicOps} implementation for {@code byte[]} data.
 * <p>
 * Provides methods to encode and decode primitive types, lists, and maps into raw byte arrays
 * with length prefixes where necessary.
 * <p>
 * This is a singleton implementation; use {@link #INSTANCE}.
 */
public class ByteOps extends DynamicOps<byte[]> {

    /** Singleton instance of {@link ByteOps}. */
    public static final ByteOps INSTANCE = new ByteOps();

    /** Private constructor to enforce singleton usage. */
    private ByteOps() {}

    /** {@inheritDoc} Encodes a UTF-8 string with a 4-byte length prefix. */
    @Override
    public byte[] createString(String value) {
        byte[] data = value.getBytes(StandardCharsets.UTF_8);
        return withLengthPrefix(data);
    }

    /** {@inheritDoc} Encodes an int as 4 bytes in big-endian order. */
    @Override
    public byte[] createInt(int value) {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putInt(value);
        return buf.array();
    }

    /** {@inheritDoc} Encodes a long as 8 bytes in big-endian order. */
    @Override
    public byte[] createLong(long value) {
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.putLong(value);
        return buf.array();
    }

    /** {@inheritDoc} Encodes a float as 4 bytes in big-endian order. */
    @Override
    public byte[] createFloat(float value) {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putFloat(value);
        return buf.array();
    }

    /** {@inheritDoc} Encodes a double as 8 bytes in big-endian order. */
    @Override
    public byte[] createDouble(double value) {
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.putDouble(value);
        return buf.array();
    }

    /** {@inheritDoc} Encodes a boolean as a single byte (1 for true, 0 for false). */
    @Override
    public byte[] createBoolean(boolean value) {
        return new byte[]{ (byte)(value ? 1 : 0) };
    }

    /** {@inheritDoc} Encodes a list of byte arrays with size prefixes and overall count. */
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

    /** {@inheritDoc} Encodes a map of byte arrays with size prefixes for keys and values. */
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

    /** {@inheritDoc} Decodes a UTF-8 string from a byte array with a length prefix. */
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

    /** {@inheritDoc} Decodes a 4-byte int. */
    @Override
    public Optional<Integer> getIntValue(byte[] input) {
        if (input.length != 4) return Optional.empty();
        return Optional.of(ByteBuffer.wrap(input).getInt());
    }

    /** {@inheritDoc} Decodes an 8-byte long. */
    @Override
    public Optional<Long> getLongValue(byte[] input) {
        if (input.length != 8) return Optional.empty();
        return Optional.of(ByteBuffer.wrap(input).getLong());
    }

    /** {@inheritDoc} Decodes a 4-byte float. */
    @Override
    public Optional<Float> getFloatValue(byte[] input) {
        if (input.length != 4) return Optional.empty();
        return Optional.of(ByteBuffer.wrap(input).getFloat());
    }

    /** {@inheritDoc} Decodes an 8-byte double. */
    @Override
    public Optional<Double> getDoubleValue(byte[] input) {
        if (input.length != 8) return Optional.empty();
        return Optional.of(ByteBuffer.wrap(input).getDouble());
    }

    /** {@inheritDoc} Decodes a boolean from a single byte. */
    @Override
    public Optional<Boolean> getBooleanValue(byte[] input) {
        if (input.length != 1) return Optional.empty();
        return Optional.of(input[0] != 0);
    }

    /** {@inheritDoc} Decodes a list of byte arrays using size prefixes. */
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

    /** {@inheritDoc} Decodes a map of byte arrays using size prefixes for keys and values. */
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

    /** {@inheritDoc} Returns an empty byte array. */
    @Override
    public byte[] empty() {
        return new byte[0];
    }

    /**
     * Prepends a 4-byte length prefix to the given byte array.
     *
     * @param data the data to prefix
     * @return a new byte array with length prefix
     */
    private byte[] withLengthPrefix(byte[] data) {
        ByteBuffer buf = ByteBuffer.allocate(4 + data.length);
        buf.putInt(data.length).put(data);
        return buf.array();
    }
}
