package com.github.darksoulq.abyssallib.common.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * A wrapper for {@link CompoundTag} that provides convenient
 * methods to store and retrieve common data types.
 */
public class CTag {
    /**
     * The underlying {@link CompoundTag}.
     */
    private final CompoundTag baseTag;

    /**
     * Constructs a new, empty {@code CTag}.
     */
    public CTag() {
        baseTag = new CompoundTag();
    }
    /**
     * Constructs a new {@code CTag} wrapping the given {@link CompoundTag}.
     *
     * @param base the tag to wrap
     */
    public CTag(CompoundTag base) {
        baseTag = base;
    }

    /**
     * Sets a {@link String} value for the given key.
     *
     * @param key   the key
     * @param value the value
     */
    public void set(String key, String value) {
        baseTag.putString(key, value);
    }
    /**
     * Sets an {@code int} value for the given key.
     *
     * @param key   the key
     * @param value the value
     */
    public void set(String key, int value) {
        baseTag.putInt(key, value);
    }
    /**
     * Sets an {@code int[]} value for the given key.
     *
     * @param key   the key
     * @param value the array value
     */
    public void set(String key, int[] value) {
        baseTag.putIntArray(key, value);
    }
    /**
     * Sets a {@code boolean} value for the given key.
     *
     * @param key   the key
     * @param value the value
     */
    public void set(String key, boolean value) {
        baseTag.putBoolean(key, value);
    }
    /**
     * Sets a {@code float} value for the given key.
     *
     * @param key   the key
     * @param value the value
     */
    public void set(String key, float value) {
        baseTag.putFloat(key, value);
    }
    /**
     * Sets a {@code byte} value for the given key.
     *
     * @param key   the key
     * @param value the value
     */
    public void set(String key, byte value) {
        baseTag.putByte(key, value);
    }
    /**
     * Sets a {@code byte[]} value for the given key.
     *
     * @param key   the key
     * @param value the array value
     */
    public void set(String key, byte[] value) {
        baseTag.putByteArray(key, value);
    }

    /**
     * Sets a {@code CTag} value for the given key
     * @param key the key
     * @param compound the value
     */
    public void set(String key, CTag compound) {
        baseTag.put(key, compound.baseTag);
    }

    /**
     * Checks whether the given key exists in this tag.
     *
     * @param key the key
     * @return {@code true} if the key is present, otherwise {@code false}
     */
    public boolean has(String key) {
        return baseTag.contains(key);
    }

    /**
     * Gets the {@link String} value stored for the given key, if present.
     *
     * @param key the key
     * @return an {@link Optional} containing the value, or empty if not present
     */
    public Optional<String> getString(String key) {
        return baseTag.getString(key);
    }
    /**
     * Gets the {@code int} value stored for the given key, if present.
     *
     * @param key the key
     * @return an {@link Optional} containing the value, or empty if not present
     */
    public Optional<Integer> getInt(String key) {
        return baseTag.getInt(key);
    }
    /**
     * Gets the {@code int[]} value stored for the given key, if present.
     *
     * @param key the key
     * @return an {@link Optional} containing the array, or empty if not present
     */
    public Optional<int[]> getIntArray(String key) {
        return baseTag.getIntArray(key);
    }
    /**
     * Gets the {@code boolean} value stored for the given key, if present.
     *
     * @param key the key
     * @return an {@link Optional} containing the value, or empty if not present
     */
    public Optional<Boolean> getBoolean(String key) {
        return baseTag.getBoolean(key);
    }
    /**
     * Gets the {@code float} value stored for the given key, if present.
     *
     * @param key the key
     * @return an {@link Optional} containing the value, or empty if not present
     */
    public Optional<Float> getFloat(String key) {
        return baseTag.getFloat(key);
    }
    /**
     * Gets the {@code byte} value stored for the given key, if present.
     *
     * @param key the key
     * @return an {@link Optional} containing the value, or empty if not present
     */
    public Optional<Byte> getByte(String key) {
        return baseTag.getByte(key);
    }
    /**
     * Gets the {@code byte[]} value stored for the given key, if present.
     *
     * @param key the key
     * @return an {@link Optional} containing the array, or empty if not present
     */
    public Optional<byte[]> getByteArray(String key) {
        return baseTag.getByteArray(key);
    }

    /**
     * Gets the {@code CTag} value stored for the given key, if present
     * @param key the key
     * @return an {@link Optional} containing the CTag, or empty if not present
     */
    public Optional<CTag> getCompound(String key) {
        CTag tag = null;
        Optional<CompoundTag> compound = baseTag.getCompound(key);
        if (compound.isEmpty()) return Optional.empty();
        tag = new CTag(compound.get());
        return Optional.of(tag);
    }

    public void clear() {
        for (String key : baseTag.keySet()) {
            baseTag.remove(key);
        }
    }

    /**
     * Returns the underlying {@link CompoundTag}.
     *
     * @return the wrapped tag
     */
    public CompoundTag toVanilla() {
        return baseTag;
    }

    public static CTag getCTag(ItemStack stack) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        CustomData dta = nms.get(DataComponents.CUSTOM_DATA);
        if (dta == null) dta = CustomData.EMPTY;

        CompoundTag tag = dta.copyTag();
        if (tag.getCompound("CustomData").isPresent()) {
            CompoundTag custom = tag.getCompound("CustomData").get();
            return new CTag(custom);
        } else {
            tag.put("CustomData", new CompoundTag());
            return new CTag(tag.getCompound("CustomData").get());
        }
    }
    public static void setCTag(CTag container, ItemStack stack) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        CustomData data = nms.get(DataComponents.CUSTOM_DATA);
        if (data == null) data = CustomData.EMPTY;
        CompoundTag tag = data.copyTag();
        tag.put("CustomData", container.toVanilla());
        data = CustomData.of(tag);
        nms.set(DataComponents.CUSTOM_DATA, data);
        ItemStack updated = CraftItemStack.asBukkitCopy(nms);
        stack.setItemMeta(updated.getItemMeta());
    }
}
