package com.github.darksoulq.abyssallib.common.serialization.ops;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import net.minecraft.nbt.*;

import java.util.*;

/**
 * An implementation of {@link DynamicOps} for Minecraft's NBT {@link Tag} structure.
 * <p>
 * This class facilitates the direct serialization of objects into native NBT tags
 * (CompoundTag, ListTag, IntTag, etc.) without intermediate JSON conversion.
 */
public class NbtOps extends DynamicOps<Tag> {

    /** Singleton instance of NbtOps. */
    public static final NbtOps INSTANCE = new NbtOps();

    private NbtOps() {}

    /**
     * Creates a {@link StringTag} from a string value.
     *
     * @param value The string to wrap.
     * @return The resulting StringTag.
     */
    @Override
    public Tag createString(String value) {
        return StringTag.valueOf(value);
    }

    /**
     * Creates an {@link IntTag} from an integer value.
     *
     * @param value The integer to wrap.
     * @return The resulting IntTag.
     */
    @Override
    public Tag createInt(int value) {
        return IntTag.valueOf(value);
    }

    /**
     * Creates a {@link LongTag} from a long value.
     *
     * @param value The long to wrap.
     * @return The resulting LongTag.
     */
    @Override
    public Tag createLong(long value) {
        return LongTag.valueOf(value);
    }

    /**
     * Creates a {@link FloatTag} from a float value.
     *
     * @param value The float to wrap.
     * @return The resulting FloatTag.
     */
    @Override
    public Tag createFloat(float value) {
        return FloatTag.valueOf(value);
    }

    /**
     * Creates a {@link DoubleTag} from a double value.
     *
     * @param value The double to wrap.
     * @return The resulting DoubleTag.
     */
    @Override
    public Tag createDouble(double value) {
        return DoubleTag.valueOf(value);
    }

    /**
     * Creates a {@link ByteTag} representing a boolean value.
     *
     * @param value The boolean to wrap (1 for true, 0 for false).
     * @return The resulting ByteTag.
     */
    @Override
    public Tag createBoolean(boolean value) {
        return ByteTag.valueOf(value);
    }

    /**
     * Creates a {@link ListTag} from a list of tags.
     *
     * @param elements The tags to include in the list.
     * @return A ListTag containing the elements.
     */
    @Override
    public Tag createList(List<Tag> elements) {
        ListTag list = new ListTag();
        list.addAll(elements);
        return list;
    }

    /**
     * Creates a {@link CompoundTag} from a map of tags.
     * <p>
     * Keys in the map must be convertible to strings.
     *
     * @param map The map of key-value tags.
     * @return A CompoundTag containing the map entries.
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
     * Retrieves a string value from a tag.
     *
     * @param input The tag to inspect.
     * @return An Optional containing the string if present.
     */
    @Override
    public Optional<String> getStringValue(Tag input) {
        return input.asString();
    }

    /**
     * Retrieves an integer value from a tag.
     *
     * @param input The tag to inspect.
     * @return An Optional containing the integer if present.
     */
    @Override
    public Optional<Integer> getIntValue(Tag input) {
        return input.asInt();
    }

    /**
     * Retrieves a long value from a tag.
     *
     * @param input The tag to inspect.
     * @return An Optional containing the long if present.
     */
    @Override
    public Optional<Long> getLongValue(Tag input) {
        return input.asLong();
    }

    /**
     * Retrieves a float value from a tag.
     *
     * @param input The tag to inspect.
     * @return An Optional containing the float if present.
     */
    @Override
    public Optional<Float> getFloatValue(Tag input) {
        return input.asFloat();
    }

    /**
     * Retrieves a double value from a tag.
     *
     * @param input The tag to inspect.
     * @return An Optional containing the double if present.
     */
    @Override
    public Optional<Double> getDoubleValue(Tag input) {
        return input.asDouble();
    }

    /**
     * Retrieves a boolean value from a tag.
     * <p>
     * Typically parses a ByteTag where 0 is false and non-zero is true.
     *
     * @param input The tag to inspect.
     * @return An Optional containing the boolean if present.
     */
    @Override
    public Optional<Boolean> getBooleanValue(Tag input) {
        return input.asByte().map(b -> b != 0);
    }

    /**
     * Retrieves a list of tags from a tag.
     *
     * @param input The tag to inspect.
     * @return An Optional containing the list if the tag is a list type.
     */
    @Override
    public Optional<List<Tag>> getList(Tag input) {
        return input.asList().map(ArrayList::new);
    }

    /**
     * Retrieves a map of tags from a tag.
     *
     * @param input The tag to inspect.
     * @return An Optional containing the map if the tag is a compound type.
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
     * Returns an empty tag representing no value (EndTag).
     *
     * @return The singleton EndTag instance.
     */
    @Override
    public Tag empty() {
        return EndTag.INSTANCE;
    }
}