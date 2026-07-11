package com.github.darksoulq.abyssallib.common.serialization.ops;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * A {@link DynamicOps} implementation that stores serialized values as raw
 * byte arrays.
 * <p>
 * Values are encoded using a simple binary format. Variable-length structures
 * such as strings, lists, and maps are prefixed with their length or entry
 * count, while primitive numeric values are stored using their standard binary
 * representation.
 */
public class ByteOps extends DynamicOps<byte[]> {

    /**
     * Shared singleton instance.
     */
    public static final ByteOps INSTANCE = new ByteOps();

    /**
     * Creates a new {@code ByteOps} instance.
     */
    private ByteOps() {
    }

    /**
     * Indicates that this format benefits from compressed map encoding.
     *
     * @return {@code true}
     */
    @Override
    public boolean compressMaps() {
        return true;
    }

    /**
     * Encodes a string as UTF-8 bytes prefixed by its length.
     *
     * @param value the string to encode
     * @return the encoded byte array
     */
    @Override
    public byte[] createString(String value) {
        byte[] data = value.getBytes(StandardCharsets.UTF_8);
        return withLengthPrefix(data);
    }

    /**
     * Encodes a byte value.
     *
     * @param value the value to encode
     * @return the encoded byte array
     */
    @Override
    public byte[] createByte(byte value) {
        return new byte[]{value};
    }

    /**
     * Encodes a short value using big-endian byte order.
     *
     * @param value the value to encode
     * @return the encoded byte array
     */
    @Override
    public byte[] createShort(short value) {
        ByteBuffer buf = ByteBuffer.allocate(2);
        buf.putShort(value);
        return buf.array();
    }

    /**
     * Encodes an integer value using big-endian byte order.
     *
     * @param value the value to encode
     * @return the encoded byte array
     */
    @Override
    public byte[] createInt(int value) {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putInt(value);
        return buf.array();
    }

    /**
     * Encodes a long value using big-endian byte order.
     *
     * @param value the value to encode
     * @return the encoded byte array
     */
    @Override
    public byte[] createLong(long value) {
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.putLong(value);
        return buf.array();
    }

    /**
     * Encodes a float value.
     *
     * @param value the value to encode
     * @return the encoded byte array
     */
    @Override
    public byte[] createFloat(float value) {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putFloat(value);
        return buf.array();
    }

    /**
     * Encodes a double value.
     *
     * @param value the value to encode
     * @return the encoded byte array
     */
    @Override
    public byte[] createDouble(double value) {
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.putDouble(value);
        return buf.array();
    }

    /**
     * Encodes a boolean value.
     *
     * @param value the value to encode
     * @return a single-byte representation of the boolean
     */
    @Override
    public byte[] createBoolean(boolean value) {
        return new byte[]{(byte) (value ? 1 : 0)};
    }

    /**
     * Encodes a list of serialized values.
     * <p>
     * The resulting format stores the element count followed by each element
     * prefixed with its length.
     *
     * @param elements the serialized list elements
     * @return the encoded list
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
     * Encodes a map of serialized key-value pairs.
     * <p>
     * The resulting format stores the entry count followed by each key and
     * value prefixed with its length.
     *
     * @param map the serialized map entries
     * @return the encoded map
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
     * Attempts to decode a UTF-8 string from a length-prefixed byte array.
     *
     * @param input the encoded byte array
     * @return the decoded string, if the input is valid
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
     * Attempts to decode a numeric value based on the size of the byte array.
     *
     * @param input the encoded byte array
     * @return the decoded number, if the input matches a supported numeric size
     */
    @Override
    public Optional<Number> getNumberValue(byte[] input) {
        if (input.length == 1) return Optional.of(input[0]);
        if (input.length == 2) return Optional.of(ByteBuffer.wrap(input).getShort());
        if (input.length == 4) return Optional.of(ByteBuffer.wrap(input).getInt());
        if (input.length == 8) return Optional.of(ByteBuffer.wrap(input).getLong());
        return Optional.empty();
    }

    /**
     * Attempts to decode an integer value.
     *
     * @param input the encoded byte array
     * @return the decoded integer, if the input contains a valid integer
     */
    @Override
    public Optional<Integer> getIntValue(byte[] input) {
        if (input.length != 4) return Optional.empty();
        return Optional.of(ByteBuffer.wrap(input).getInt());
    }

    /**
     * Attempts to decode a long value.
     *
     * @param input the encoded byte array
     * @return the decoded long, if the input contains a valid long
     */
    @Override
    public Optional<Long> getLongValue(byte[] input) {
        if (input.length != 8) return Optional.empty();
        return Optional.of(ByteBuffer.wrap(input).getLong());
    }

    /**
     * Attempts to decode a float value.
     *
     * @param input the encoded byte array
     * @return the decoded float, if the input contains a valid float
     */
    @Override
    public Optional<Float> getFloatValue(byte[] input) {
        if (input.length != 4) return Optional.empty();
        return Optional.of(ByteBuffer.wrap(input).getFloat());
    }

    /**
     * Attempts to decode a double value.
     *
     * @param input the encoded byte array
     * @return the decoded double, if the input contains a valid double
     */
    @Override
    public Optional<Double> getDoubleValue(byte[] input) {
        if (input.length != 8) return Optional.empty();
        return Optional.of(ByteBuffer.wrap(input).getDouble());
    }

    /**
     * Attempts to decode a boolean value.
     *
     * @param input the encoded byte array
     * @return the decoded boolean, if the input contains a valid boolean
     */
    @Override
    public Optional<Boolean> getBooleanValue(byte[] input) {
        if (input.length != 1) return Optional.empty();
        return Optional.of(input[0] != 0);
    }

    /**
     * Attempts to decode a serialized list.
     *
     * @param input the encoded byte array
     * @return the decoded list, if the input contains a valid list
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
     * Attempts to decode a serialized map.
     *
     * @param input the encoded byte array
     * @return the decoded map, if the input contains a valid map
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
     * Returns the decoded string keys contained in a serialized map.
     *
     * @param input the encoded map value
     * @return the decoded keys, if the input contains a valid map
     */
    @Override
    public Optional<Iterable<String>> getKeys(byte[] input) {
        return getMap(input).map(map -> {
            List<String> keys = new ArrayList<>();
            for (byte[] k : map.keySet()) {
                getStringValue(k).ifPresent(keys::add);
            }
            return keys;
        });
    }

    /**
     * Returns the number of entries contained in a serialized list or map.
     *
     * @param input the encoded value
     * @return the collection size, if the value is a valid list or map
     */
    @Override
    public OptionalInt size(byte[] input) {
        Optional<List<byte[]>> listOpt = getList(input);
        if (listOpt.isPresent()) return OptionalInt.of(listOpt.get().size());

        Optional<Map<byte[], byte[]>> mapOpt = getMap(input);
        return mapOpt.map(map -> OptionalInt.of(map.size())).orElseGet(OptionalInt::empty);

    }

    /**
     * Creates a copy of the provided byte array.
     *
     * @param input the array to copy
     * @return a new array containing the same bytes
     */
    @Override
    public byte[] copy(byte[] input) {
        return Arrays.copyOf(input, input.length);
    }

    /**
     * Returns the empty value representation for this format.
     *
     * @return an empty byte array
     */
    @Override
    public byte[] empty() {
        return new byte[0];
    }

    /**
     * Prefixes a byte array with its length.
     *
     * @param data the payload to wrap
     * @return a new array containing the payload length followed by the payload
     */
    private byte[] withLengthPrefix(byte[] data) {
        ByteBuffer buf = ByteBuffer.allocate(4 + data.length);
        buf.putInt(data.length).put(data);
        return buf.array();
    }
}