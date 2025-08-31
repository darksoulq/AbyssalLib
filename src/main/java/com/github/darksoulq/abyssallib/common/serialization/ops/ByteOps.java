package com.github.darksoulq.abyssallib.common.serialization.ops;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ByteOps extends DynamicOps<byte[]> {

    public static final ByteOps INSTANCE = new ByteOps();

    private ByteOps() {}

    @Override
    public byte[] createString(String value) {
        byte[] data = value.getBytes(StandardCharsets.UTF_8);
        return withLengthPrefix(data);
    }

    @Override
    public byte[] createInt(int value) {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putInt(value);
        return buf.array();
    }

    @Override
    public byte[] createLong(long value) {
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.putLong(value);
        return buf.array();
    }

    @Override
    public byte[] createFloat(float value) {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putFloat(value);
        return buf.array();
    }

    @Override
    public byte[] createDouble(double value) {
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.putDouble(value);
        return buf.array();
    }

    @Override
    public byte[] createBoolean(boolean value) {
        return new byte[]{ (byte)(value ? 1 : 0) };
    }

    @Override
    public byte[] createList(List<byte[]> elements) {
        int total = 4; // length prefix
        for (byte[] elem : elements) {
            total += 4 + elem.length; // size prefix + data
        }
        ByteBuffer buf = ByteBuffer.allocate(total);
        buf.putInt(elements.size());
        for (byte[] elem : elements) {
            buf.putInt(elem.length);
            buf.put(elem);
        }
        return buf.array();
    }

    @Override
    public byte[] createMap(Map<byte[], byte[]> map) {
        int total = 4; // entry count
        for (Map.Entry<byte[], byte[]> e : map.entrySet()) {
            total += 4 + e.getKey().length; // key size + data
            total += 4 + e.getValue().length; // val size + data
        }
        ByteBuffer buf = ByteBuffer.allocate(total);
        buf.putInt(map.size());
        for (Map.Entry<byte[], byte[]> e : map.entrySet()) {
            buf.putInt(e.getKey().length).put(e.getKey());
            buf.putInt(e.getValue().length).put(e.getValue());
        }
        return buf.array();
    }

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

    @Override
    public Optional<Integer> getIntValue(byte[] input) {
        if (input.length != 4) return Optional.empty();
        return Optional.of(ByteBuffer.wrap(input).getInt());
    }

    @Override
    public Optional<Long> getLongValue(byte[] input) {
        if (input.length != 8) return Optional.empty();
        return Optional.of(ByteBuffer.wrap(input).getLong());
    }

    @Override
    public Optional<Float> getFloatValue(byte[] input) {
        if (input.length != 4) return Optional.empty();
        return Optional.of(ByteBuffer.wrap(input).getFloat());
    }

    @Override
    public Optional<Double> getDoubleValue(byte[] input) {
        if (input.length != 8) return Optional.empty();
        return Optional.of(ByteBuffer.wrap(input).getDouble());
    }

    @Override
    public Optional<Boolean> getBooleanValue(byte[] input) {
        if (input.length != 1) return Optional.empty();
        return Optional.of(input[0] != 0);
    }

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

    @Override
    public byte[] empty() {
        return new byte[0];
    }

    private byte[] withLengthPrefix(byte[] data) {
        ByteBuffer buf = ByteBuffer.allocate(4 + data.length);
        buf.putInt(data.length).put(data);
        return buf.array();
    }
}
