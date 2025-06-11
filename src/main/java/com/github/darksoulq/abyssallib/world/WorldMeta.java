package com.github.darksoulq.abyssallib.world;

import com.github.darksoulq.abyssallib.world.level.data.Identifier;
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
        world.getPersistentDataContainer().set(key.toNamespace(), PersistentDataType.STRING, value);
    }

    public String getString(Identifier key) {
        return world.getPersistentDataContainer().get(key.toNamespace(), PersistentDataType.STRING);
    }

    public void setInt(Identifier key, int value) {
        world.getPersistentDataContainer().set(key.toNamespace(), PersistentDataType.INTEGER, value);
    }

    public Integer getInt(Identifier key) {
        return world.getPersistentDataContainer().get(key.toNamespace(), PersistentDataType.INTEGER);
    }

    public void setBoolean(Identifier key, boolean value) {
        world.getPersistentDataContainer().set(key.toNamespace(), PersistentDataType.BYTE, (byte) (value ? 1 : 0));
    }

    public Boolean getBoolean(Identifier key) {
        Byte b = world.getPersistentDataContainer().get(key.toNamespace(), PersistentDataType.BYTE);
        return b != null && b != 0;
    }

    public void remove(Identifier key) {
        world.getPersistentDataContainer().remove(key.toNamespace());
    }

    public boolean has(Identifier key) {
        return world.getPersistentDataContainer().has(key.toNamespace(), PersistentDataType.STRING)
                || world.getPersistentDataContainer().has(key.toNamespace(), PersistentDataType.INTEGER)
                || world.getPersistentDataContainer().has(key.toNamespace(), PersistentDataType.BYTE);
    }

    public World getWorld() {
        return world;
    }
}
