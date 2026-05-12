package com.github.darksoulq.abyssallib.server.cooldown;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.UUID;

public record CooldownScope(Type type, UUID id) {

    private static final UUID GLOBAL_UUID = new UUID(0, 0);

    public enum Type {
        GLOBAL,
        PLAYER,
        ENTITY,
        WORLD,
        CUSTOM
    }

    public static CooldownScope global() {
        return new CooldownScope(Type.GLOBAL, GLOBAL_UUID);
    }

    public static CooldownScope player(Player player) {
        return new CooldownScope(Type.PLAYER, player.getUniqueId());
    }

    public static CooldownScope entity(Entity entity) {
        return new CooldownScope(Type.ENTITY, entity.getUniqueId());
    }

    public static CooldownScope world(World world) {
        return new CooldownScope(Type.WORLD, world.getUID());
    }

    public static CooldownScope custom(UUID id) {
        return new CooldownScope(Type.CUSTOM, id);
    }
}