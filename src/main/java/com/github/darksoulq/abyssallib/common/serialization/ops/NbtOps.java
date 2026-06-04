package com.github.darksoulq.abyssallib.common.serialization.ops;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import net.minecraft.nbt.*;

import java.util.*;

/**
 * A {@link DynamicOps} implementation for Minecraft's NBT {@link Tag} system.
 * <p>
 * This implementation enables direct serialization and deserialization of values
 * using native NBT structures (CompoundTag, ListTag, numeric tags, etc.) without
 * intermediate conversion layers.
 */
public class NbtOps extends DynamicOps<Tag> {

    /**
     * Singleton instance of {@code NbtOps}.
     */
    public static final NbtOps INSTANCE = new NbtOps();

    /**
     * Creates a new {@code NbtOps} instance.
     */
    private NbtOps() {}

    /**
     * Creates a string tag.
     *
     * @param value string value
     * @return StringTag wrapping the value
     */
    @Override
    public Tag createString(String value) {
        return StringTag.valueOf(value);
    }

    /**
     * Creates a byte tag.
     *
     * @param value byte value
     * @return ByteTag wrapping the value
     */
    @Override
    public Tag createByte(byte value) {
        return ByteTag.valueOf(value);
    }

    /**
     * Creates a short tag.
     *
     * @param value short value
     * @return ShortTag wrapping the value
     */
    @Override
    public Tag createShort(short value) {
        return ShortTag.valueOf(value);
    }

    /**
     * Creates an int tag.
     *
     * @param value int value
     * @return IntTag wrapping the value
     */
    @Override
    public Tag createInt(int value) {
        return IntTag.valueOf(value);
    }

    /**
     * Creates a long tag.
     *
     * @param value long value
     * @return LongTag wrapping the value
     */
    @Override
    public Tag createLong(long value) {
        return LongTag.valueOf(value);
    }

    /**
     * Creates a float tag.
     *
     * @param value float value
     * @return FloatTag wrapping the value
     */
    @Override
    public Tag createFloat(float value) {
        return FloatTag.valueOf(value);
    }

    /**
     * Creates a double tag.
     *
     * @param value double value
     * @return DoubleTag wrapping the value
     */
    @Override
    public Tag createDouble(double value) {
        return DoubleTag.valueOf(value);
    }

    /**
     * Creates a boolean tag (stored as a byte).
     *
     * @param value boolean value
     * @return ByteTag representing true/false
     */
    @Override
    public Tag createBoolean(boolean value) {
        return ByteTag.valueOf(value);
    }

    /**
     * Creates a list tag.
     *
     * @param elements list of tags
     * @return ListTag containing elements
     */
    @Override
    public Tag createList(List<Tag> elements) {
        ListTag list = new ListTag();
        list.addAll(elements);
        return list;
    }

    /**
     * Creates a compound tag from key-value pairs.
     *
     * @param map map of tags
     * @return CompoundTag representing the map
     */
    @Override
    public Tag createMap(Map<Tag, Tag> map) {
        CompoundTag tag = new CompoundTag();
        for (Map.Entry<Tag, Tag> entry : map.entrySet()) {
            entry.getKey().asString().ifPresent(key -> tag.put(key, entry.getValue()));
        }
        return tag;
    }

    /**
     * Extracts a string value from a tag.
     *
     * @param input input tag
     * @return optional string value
     */
    @Override
    public Optional<String> getStringValue(Tag input) {
        return input.asString();
    }

    /**
     * Extracts a numeric value from a tag.
     *
     * @param input input tag
     * @return optional number value
     */
    @Override
    public Optional<Number> getNumberValue(Tag input) {
        return input instanceof NumericTag n ? n.asNumber() : Optional.empty();
    }

    /**
     * Extracts an integer value from a tag.
     *
     * @param input input tag
     * @return optional integer value
     */
    @Override
    public Optional<Integer> getIntValue(Tag input) {
        return input.asInt();
    }

    /**
     * Extracts a long value from a tag.
     *
     * @param input input tag
     * @return optional long value
     */
    @Override
    public Optional<Long> getLongValue(Tag input) {
        return input.asLong();
    }

    /**
     * Extracts a float value from a tag.
     *
     * @param input input tag
     * @return optional float value
     */
    @Override
    public Optional<Float> getFloatValue(Tag input) {
        return input.asFloat();
    }

    /**
     * Extracts a double value from a tag.
     *
     * @param input input tag
     * @return optional double value
     */
    @Override
    public Optional<Double> getDoubleValue(Tag input) {
        return input.asDouble();
    }

    /**
     * Extracts a boolean value from a tag.
     *
     * @param input input tag
     * @return optional boolean value
     */
    @Override
    public Optional<Boolean> getBooleanValue(Tag input) {
        return input.asByte().map(b -> b != 0);
    }

    /**
     * Extracts a list from a tag.
     *
     * @param input input tag
     * @return optional list of tags
     */
    @Override
    public Optional<List<Tag>> getList(Tag input) {
        return input.asList().map(ArrayList::new);
    }

    /**
     * Extracts a map from a compound tag.
     *
     * @param input input tag
     * @return optional map of tags
     */
    @Override
    public Optional<Map<Tag, Tag>> getMap(Tag input) {
        return input.asCompound().map(compound -> {
            Map<Tag, Tag> map = new LinkedHashMap<>();
            for (String key : compound.keySet()) {
                map.put(StringTag.valueOf(key), compound.get(key));
            }
            return map;
        });
    }

    /**
     * Returns keys from a compound tag.
     *
     * @param input input tag
     * @return iterable of keys
     */
    @Override
    public Optional<Iterable<String>> getKeys(Tag input) {
        return input instanceof CompoundTag c ? Optional.of(c.keySet()) : Optional.empty();
    }

    /**
     * Returns the size of a list or compound tag.
     *
     * @param input input tag
     * @return optional size
     */
    @Override
    public OptionalInt size(Tag input) {
        if (input instanceof CompoundTag c) return OptionalInt.of(c.size());
        if (input instanceof CollectionTag c) return OptionalInt.of(c.size());
        return OptionalInt.empty();
    }

    /**
     * Creates a deep copy of a tag.
     *
     * @param input input tag
     * @return copied tag
     */
    @Override
    public Tag copy(Tag input) {
        return input.copy();
    }

    /**
     * Returns the empty NBT value.
     *
     * @return EndTag instance
     */
    @Override
    public Tag empty() {
        return EndTag.INSTANCE;
    }
}