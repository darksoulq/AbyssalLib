package com.github.darksoulq.abyssallib.world.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

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

    public void set(String key, double value) {
        baseTag.putDouble(key, value);
    }

    public void set(String key, byte value) {
        baseTag.putByte(key, value);
    }

    public void set(String key, byte[] value) {
        baseTag.putByteArray(key, value);
    }

    public void set(String key, short value) {
        baseTag.putShort(key, value);
    }

    public void set(String key, long value) {
        baseTag.putLong(key, value);
    }

    public void set(String key, long[] value) {
        baseTag.putLongArray(key, value);
    }

    public void set(String key, CTag compound) {
        baseTag.put(key, compound.baseTag);
    }

    public void set(String key, Tag tag) {
        baseTag.put(key, tag);
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

    public Optional<Double> getDouble(String key) {
        return baseTag.getDouble(key);
    }

    public Optional<Byte> getByte(String key) {
        return baseTag.getByte(key);
    }

    public Optional<byte[]> getByteArray(String key) {
        return baseTag.getByteArray(key);
    }

    public Optional<Short> getShort(String key) {
        return baseTag.getShort(key);
    }

    public Optional<Long> getLong(String key) {
        return baseTag.getLong(key);
    }

    public Optional<long[]> getLongArray(String key) {
        return baseTag.getLongArray(key);
    }

    public Optional<Tag> getTag(String key) {
        return Optional.ofNullable(baseTag.get(key));
    }

    public Optional<CTag> getCompound(String key) {
        return baseTag.getCompound(key).map(CTag::new);
    }

    public void clear() {
        baseTag.keySet().clear();
    }

    public CompoundTag toVanilla() {
        return baseTag;
    }

    public static CTag getCTag(ItemStack stack) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        CustomData dta = nms.get(DataComponents.CUSTOM_DATA);
        if (dta == null) dta = CustomData.EMPTY;

        CompoundTag tag = dta.copyTag();
        Optional<CompoundTag> customData = tag.getCompound("CustomData");

        if (customData.isPresent()) {
            return new CTag(customData.get());
        } else {
            CompoundTag custom = new CompoundTag();
            tag.put("CustomData", custom);
            return new CTag(custom);
        }
    }

    public static void setCTag(CTag container, ItemStack stack) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        CustomData data = nms.get(DataComponents.CUSTOM_DATA);
        if (data == null) data = CustomData.EMPTY;

        CompoundTag tag = data.copyTag();
        tag.put("CustomData", container.toVanilla());

        nms.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        ItemStack updated = CraftItemStack.asBukkitCopy(nms);
        stack.setItemMeta(updated.getItemMeta());
    }
}