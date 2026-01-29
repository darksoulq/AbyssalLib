package com.github.darksoulq.abyssallib.world.entity.internal;

import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.entity.CustomEntity;
import com.github.darksoulq.abyssallib.world.entity.SpawnCategory;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class NaturalSpawnRegistry {
    private static final Map<SpawnCategory, List<CustomEntity<? extends LivingEntity>>> REGISTRY = new EnumMap<>(SpawnCategory.class);

    static {
        for (SpawnCategory category : SpawnCategory.values()) {
            REGISTRY.put(category, new ArrayList<>());
        }
    }

    public static void load() {
        Registries.ENTITIES.getAll().forEach((s, e) -> {
            register(e);
        });
    }

    public static void register(CustomEntity<? extends LivingEntity> entity) {
        if (entity.getSpawnSettings() == null) return;
        REGISTRY.get(entity.getCategory()).add(entity);
    }

    public static List<CustomEntity<? extends LivingEntity>> get(SpawnCategory category) {
        return REGISTRY.get(category);
    }
}
