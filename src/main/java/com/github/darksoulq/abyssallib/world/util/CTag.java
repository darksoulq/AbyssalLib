package com.github.darksoulq.abyssallib.world.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * A utility wrapper for Minecraft's {@link CompoundTag} (NBT) system.
 * <p>
 * This class provides a simplified API for interacting with structured data,
 * handling the conversion between Bukkit {@link ItemStack}s and NMS internal
 * {@link DataComponents#CUSTOM_DATA}.
 */
public class CTag {
    /** The underlying NMS CompoundTag being wrapped. */
    private final CompoundTag baseTag;

    /**
     * Constructs a new, empty CTag.
     */
    public CTag() {
        baseTag = new CompoundTag();
    }

    /**
     * Wraps an existing NMS {@link CompoundTag}.
     *
     * @param base The tag to wrap.
     */
    public CTag(CompoundTag base) {
        baseTag = base;
    }

    /**
     * Maps a String value to the specified key.
     * @param key   The NBT key.
     * @param value The value to store.
     */
    public void set(String key, String value) {
        baseTag.putString(key, value);
    }

    /**
     * Maps an integer value to the specified key.
     * @param key   The NBT key.
     * @param value The value to store.
     */
    public void set(String key, int value) {
        baseTag.putInt(key, value);
    }

    /**
     * Maps an integer array to the specified key.
     * @param key   The NBT key.
     * @param value The array to store.
     */
    public void set(String key, int[] value) {
        baseTag.putIntArray(key, value);
    }

    /**
     * Maps a boolean value to the specified key.
     * @param key   The NBT key.
     * @param value The value to store.
     */
    public void set(String key, boolean value) {
        baseTag.putBoolean(key, value);
    }

    /**
     * Maps a float value to the specified key.
     * @param key   The NBT key.
     * @param value The value to store.
     */
    public void set(String key, float value) {
        baseTag.putFloat(key, value);
    }

    /**
     * Maps a byte value to the specified key.
     * @param key   The NBT key.
     * @param value The value to store.
     */
    public void set(String key, byte value) {
        baseTag.putByte(key, value);
    }

    /**
     * Maps a byte array to the specified key.
     * @param key   The NBT key.
     * @param value The array to store.
     */
    public void set(String key, byte[] value) {
        baseTag.putByteArray(key, value);
    }

    /**
     * Nests another CTag under the specified key.
     * @param key      The NBT key.
     * @param compound The CTag instance to nest.
     */
    public void set(String key, CTag compound) {
        baseTag.put(key, compound.baseTag);
    }

    /**
     * Checks if the tag contains a specific key.
     * @param key The key to look for.
     * @return {@code true} if the key exists.
     */
    public boolean has(String key) {
        return baseTag.contains(key);
    }

    /** @return An Optional containing the String value if found. @param key The NBT key. */
    public Optional<String> getString(String key) {
        return baseTag.getString(key);
    }

    /** @return An Optional containing the Integer value if found. @param key The NBT key. */
    public Optional<Integer> getInt(String key) {
        return baseTag.getInt(key);
    }

    /** @return An Optional containing the Integer array if found. @param key The NBT key. */
    public Optional<int[]> getIntArray(String key) {
        return baseTag.getIntArray(key);
    }

    /** @return An Optional containing the Boolean value if found. @param key The NBT key. */
    public Optional<Boolean> getBoolean(String key) {
        return baseTag.getBoolean(key);
    }

    /** @return An Optional containing the Float value if found. @param key The NBT key. */
    public Optional<Float> getFloat(String key) {
        return baseTag.getFloat(key);
    }

    /** @return An Optional containing the Byte value if found. @param key The NBT key. */
    public Optional<Byte> getByte(String key) {
        return baseTag.getByte(key);
    }

    /** @return An Optional containing the Byte array if found. @param key The NBT key. */
    public Optional<byte[]> getByteArray(String key) {
        return baseTag.getByteArray(key);
    }

    /**
     * Retrieves a nested CTag.
     *
     * @param key The NBT key.
     * @return An Optional containing the nested CTag if it exists.
     */
    public Optional<CTag> getCompound(String key) {
        Optional<CompoundTag> compound = baseTag.getCompound(key);
        if (compound.isEmpty()) return Optional.empty();
        return Optional.of(new CTag(compound.get()));
    }

    /**
     * Removes all keys from the underlying tag.
     */
    public void clear() {
        for (String key : baseTag.keySet()) {
            baseTag.remove(key);
        }
    }

    /**
     * Exposes the underlying NMS tag.
     *
     * @return The raw {@link CompoundTag}.
     */
    public CompoundTag toVanilla() {
        return baseTag;
    }

    /**
     * Extracts custom data from a Bukkit {@link ItemStack}.
     * <p>
     * It specifically looks for a nested "CustomData" tag within the item's
     * {@link DataComponents#CUSTOM_DATA} component.
     *
     * @param stack The item to read from.
     * @return A CTag containing the persistent data.
     */
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

    /**
     * Writes a CTag's data to a Bukkit {@link ItemStack}.
     * <p>
     * This method converts the item to NMS, modifies the CUSTOM_DATA component,
     * and applies the changes back to the Bukkit item via its ItemMeta.
     *
     * @param container The data to write.
     * @param stack     The item to modify.
     */
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