package com.github.darksoulq.abyssallib.world.entity.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.database.Database;
import com.github.darksoulq.abyssallib.common.database.impl.sqlite.SqliteDatabase;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EntityManager {
    private static final Map<UUID, Entity<? extends LivingEntity>> entities = new HashMap<>();
    private static final Database database = new SqliteDatabase(new File(AbyssalLib.getInstance().getDataFolder(),
            "entities.db"));

    public static void load() {
        try {
            database.connect();

            database.executor().table("entities").create()
                    .ifNotExists()
                    .column("entity_uuid", "TEXT")
                    .column("entity_id", "TEXT")
                    .execute();

            List<Entity<? extends LivingEntity>> loaded = database.executor().table("entities").select(rs -> {
               UUID uuid = UUID.fromString(rs.getString("entity_uuid"));
               Identifier id = Identifier.of(rs.getString("entity_id"));
               Entity<? extends LivingEntity> entity = Registries.ENTITIES.get(id.toString());
               if (entity == null || (Bukkit.getEntity(entity.uuid) != null
                       && !Bukkit.getEntity(entity.uuid).isDead())) return null;
               entity.uuid = uuid;
               return entity;
            });

            loaded.forEach(e -> {
                entities.put(e.uuid, e);
            });
        } catch (Exception e) {
            AbyssalLib.getInstance().getLogger().severe("Failed to load entity database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void add(Entity<? extends LivingEntity> entity) {
        entities.put(entity.uuid, entity);
        save(entity);
    }

    public static Entity<? extends LivingEntity> get(UUID uuid) {
        return entities.get(uuid);
    }

    public static void remove(UUID uuid) {
        entities.remove(uuid);
        try {
            database.executor().table("entities").delete()
                    .where("entity_uuid", uuid)
                    .update();
        } catch (Exception e) {
            AbyssalLib.getInstance().getLogger().warning("Failed to remove entity: " + e.getMessage());
        }
    }

    public static void save(Entity<? extends LivingEntity> entity) {
        database.executor().table("entities").insert()
                .value("entity_uuid", entity.uuid)
                .value("entity_id", entity.getId())
                .execute();
    }
}
