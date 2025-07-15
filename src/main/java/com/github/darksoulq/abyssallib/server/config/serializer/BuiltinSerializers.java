package com.github.darksoulq.abyssallib.server.config.serializer;

import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.UUID;

public class BuiltinSerializers {
    public static class UUIDSerializer implements ConfigSerializer<UUID> {
        @Override
        public Object serialize(UUID v) { return v.toString(); }
        @Override
        public UUID deserialize(Object s, Field f) { return UUID.fromString((String) s); }
    }

    public static class ItemStackSerializer implements ConfigSerializer<ItemStack> {
        @Override
        public Object serialize(ItemStack value) {
            return value.serializeAsBytes();
        }

        @Override
        public ItemStack deserialize(Object in, Field field) {
            return ItemStack.deserializeBytes((byte[]) in);
        }
    }
}
