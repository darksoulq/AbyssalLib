package com.github.darksoulq.abyssallib.world;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WorldMeta {

    private static final Map<UUID, WorldMeta> CACHE = new ConcurrentHashMap<>();

    private final World world;

    private WorldMeta(World world) {
        this.world = world;
    }

    public static WorldMeta of(World world) {
        return CACHE.computeIfAbsent(world.getUID(), id -> new WorldMeta(world));
    }

    public void setString(Identifier key, String value) {
        world.getPersistentDataContainer().set(key.asNamespacedKey(), PersistentDataType.STRING, value);
    }

    public String getString(Identifier key) {
        return world.getPersistentDataContainer().get(key.asNamespacedKey(), PersistentDataType.STRING);
    }

    public void setInt(Identifier key, int value) {
        world.getPersistentDataContainer().set(key.asNamespacedKey(), PersistentDataType.INTEGER, value);
    }

    public Integer getInt(Identifier key) {
        return world.getPersistentDataContainer().get(key.asNamespacedKey(), PersistentDataType.INTEGER);
    }

    public void setBoolean(Identifier key, boolean value) {
        world.getPersistentDataContainer().set(key.asNamespacedKey(), PersistentDataType.BYTE, (byte) (value ? 1 : 0));
    }

    public Boolean getBoolean(Identifier key) {
        Byte b = world.getPersistentDataContainer().get(key.asNamespacedKey(), PersistentDataType.BYTE);
        return b != null && b != 0;
    }

    public void remove(Identifier key) {
        world.getPersistentDataContainer().remove(key.asNamespacedKey());
    }

    public boolean has(Identifier key) {
        return world.getPersistentDataContainer().has(key.asNamespacedKey(), PersistentDataType.STRING)
                || world.getPersistentDataContainer().has(key.asNamespacedKey(), PersistentDataType.INTEGER)
                || world.getPersistentDataContainer().has(key.asNamespacedKey(), PersistentDataType.BYTE);
    }

    public World getWorld() {
        return world;
    }
}
