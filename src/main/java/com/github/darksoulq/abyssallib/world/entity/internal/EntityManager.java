package com.github.darksoulq.abyssallib.world.entity.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.database.Database;
import com.github.darksoulq.abyssallib.common.database.impl.sqlite.SqliteDatabase;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.event.custom.entity.EntitySpawnEvent;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.entity.Entity;
import com.github.darksoulq.abyssallib.world.entity.SpawnCategory;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;
import java.util.function.BiPredicate;

public class EntityManager {
    public static final Random rand = new Random();

    private static final Map<UUID, Entity<? extends LivingEntity>> ENTITIES = new HashMap<>();
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
               if (entity == null) return null;
               entity.uuid = uuid;
               return entity;
            });

            loaded.forEach(e -> {
                ENTITIES.put(e.uuid, e);
            });
        } catch (Exception e) {
            AbyssalLib.getInstance().getLogger().severe("Failed to load entity database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void add(Entity<? extends LivingEntity> entity) {
        ENTITIES.put(entity.uuid, entity);
        save(entity);
    }
    public static Entity<? extends LivingEntity> get(UUID uuid) {
        return ENTITIES.get(uuid);
    }
    public static void remove(UUID uuid) {
        ENTITIES.remove(uuid);
        try {
            database.executor().table("entities").delete()
                    .where("entity_uuid", uuid.toString())
                    .update();
        } catch (Exception e) {
            AbyssalLib.getInstance().getLogger().warning("Failed to remove entity: " + e.getMessage());
        }
    }
    public static void save(Entity<? extends LivingEntity> entity) {
        database.executor().table("entities").insert()
                .value("entity_uuid", entity.uuid.toString())
                .value("entity_id", entity.getId().toString())
                .execute();
    }

    public static void restoreEntities() {
        for (Map.Entry<UUID, Entity<? extends LivingEntity>> entry : ENTITIES.entrySet()) {
            entry.getValue().onLoad();
            entry.getValue().applyGoals();
            entry.getValue().applyAttributes();
        }
        AbyssalLib.LOGGER.info("Loaded " + ENTITIES.size() + " entities");
    }

    public static void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    try {
                        runSpawnCycle(world);
                    } catch (CloneNotSupportedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }.runTaskTimer(AbyssalLib.getInstance(), 20L * 5, 20L * 5);
    }
    private static void runSpawnCycle(World world) throws CloneNotSupportedException {
        Collection<? extends Player> players = world.getPlayers();
        if (players.isEmpty()) return;

        Set<Chunk> eligibleChunks = new HashSet<>();
        for (Player player : players) {
            int chunkRadius = 128 / 16;
            Chunk center = player.getLocation().getChunk();

            for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
                for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                    Chunk chunk = world.getChunkAt(center.getX() + dx, center.getZ() + dz);
                    eligibleChunks.add(chunk);
                }
            }
        }

        int totalLoaded = world.getLoadedChunks().length;

        for (SpawnCategory category : SpawnCategory.values()) {
            int cap = switch (category) {
                case MONSTER ->  AbyssalLib.CONFIG.spawnLimits.monster.get();
                case CREATURE ->  AbyssalLib.CONFIG.spawnLimits.creature.get();
                case AMBIENT ->  AbyssalLib.CONFIG.spawnLimits.ambient.get();
                case WATER_MONSTER -> AbyssalLib.CONFIG.spawnLimits.waterMonster.get();
                case WATER_CREATURE -> AbyssalLib.CONFIG.spawnLimits.waterCreature.get();
                case WATER_AMBIENT -> AbyssalLib.CONFIG.spawnLimits.waterAmbient.get();
            };
            if (cap <= 0) continue;

            int currentCount = countEntities(world, category);
            int allowed = (cap * eligibleChunks.size()) / Math.max(totalLoaded, 1);

            if (currentCount >= allowed) continue;
            Chunk randomChunk = eligibleChunks.stream().skip(rand.nextInt(eligibleChunks.size())).findFirst().orElse(null);
            if (randomChunk == null) continue;

            Location spawnLoc = getRandomLocationInChunk(randomChunk, world);
            if (spawnLoc == null) continue;

            spawnCustomEntity(world, spawnLoc, category);
        }
    }
    private static int countEntities(World world, SpawnCategory category) {
        int count = 0;
        List<Entity<? extends LivingEntity>> entities = new ArrayList<>();
        world.getEntities().forEach(e -> entities.add(Entity.resolve(e)));
        for (Entity<? extends LivingEntity> e : entities) {
            if (e.getCategory() == category) {
                count++;
            }
        }
        return count;
    }
    private static Location getRandomLocationInChunk(Chunk chunk, World world) {
        int x = (chunk.getX() << 4) + rand.nextInt(16);
        int z = (chunk.getZ() << 4) + rand.nextInt(16);
        int y = rand.nextInt(world.getMaxHeight());

        Location loc = new Location(world, x, y, z);
        if (!world.getWorldBorder().isInside(loc)) return null;
        return loc;
    }
    private static void spawnCustomEntity(World world, Location loc, SpawnCategory category) throws CloneNotSupportedException {
        Entity.EntityEntry entry = Entity.getWeighedSpawnEntry(loc.getBlock().getBiome(), category);
        if (entry == null) return;
        for (BiPredicate<World, Location> condition : entry.entity().getSpawnConditions()) {
            if (!condition.test(world, loc)) return;
        }
        int groupSize = entry.entry().maxGroup() + rand.nextInt(entry.entry().maxGroup() - entry.entry().minGroup() + 1);
        for (int i = 0; i < groupSize; i++) {
            Location offset = loc.clone().add(
                    rand.nextInt(3) - 1,
                    0,
                    rand.nextInt(3) - 1
            );
            entry.entity().clone().spawn(offset, EntitySpawnEvent.SpawnReason.NATURAL);
        }
    }
}
