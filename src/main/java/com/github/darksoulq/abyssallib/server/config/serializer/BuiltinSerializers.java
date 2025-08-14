package com.github.darksoulq.abyssallib.server.config.serializer;

import org.bukkit.inventory.ItemStack;

import java.util.Base64;
import java.util.UUID;

public class BuiltinSerializers {
    public static class UUIDSerializer implements Serializer<UUID> {

        @Override
        public Object serialize(UUID value) {
            return value == null ? null : value.toString();
        }

        @Override
        public UUID deserialize(Object value) {
            return switch (value) {
                case null -> null;
                case UUID uuid -> uuid;
                case String s -> UUID.fromString(s);
                default -> throw new IllegalArgumentException("Cannot deserialize UUID from: " + value);
            };
        }
    }
    public static class ItemStackSerializer implements Serializer<ItemStack> {

        @Override
        public Object serialize(ItemStack value) {
            if (value == null) return null;
            try {
                byte[] bytes = value.serializeAsBytes();
                return Base64.getEncoder().encodeToString(bytes);
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize ItemStack", e);
            }
        }

        @Override
        public ItemStack deserialize(Object value) {
            if (value == null) return null;
            if (!(value instanceof String))
                throw new IllegalArgumentException("Cannot deserialize ItemStack from: " + value);
            try {
                byte[] bytes = Base64.getDecoder().decode((String) value);
                return ItemStack.deserializeBytes(bytes);
            } catch (Exception e) {
                throw new RuntimeException("Failed to deserialize ItemStack", e);
            }
        }
    }

}
