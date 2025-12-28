package com.github.darksoulq.abyssallib.world.entity.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.config.internal.PluginConfig;
import com.github.darksoulq.abyssallib.common.database.sql.Database;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.event.custom.entity.EntitySpawnEvent;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.util.TaskUtil;
import com.github.darksoulq.abyssallib.world.entity.Entity;
import com.github.darksoulq.abyssallib.world.entity.SpawnCategory;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class EntityManager {
    public static final Random RAND = new Random();

    private static final Map<UUID, Entity<? extends LivingEntity>> ENTITIES = new HashMap<>();
    private static final Map<World, EnumMap<SpawnCategory, Integer>> CATEGORY_COUNTS = new HashMap<>();
    private static final Map<World, Map<Long, Integer>> REGION_DENSITY = new HashMap<>();
    private static final Map<World, List<Chunk>> CHUNK_CACHE = new HashMap<>();
    private static final int MAX_REGION_DENSITY = 6;

    private static final Database DATABASE = new Database(new File(AbyssalLib.getInstance().getDataFolder(), "entities.db"));

    public static void load() {
        try {
            DATABASE.connect();
            DATABASE.executor().table("entities").create()
                .ifNotExists()
                .column("entity_uuid", "TEXT")
                .column("entity_id", "TEXT")
                .primaryKey("entity_uuid")
                .execute();
            List<Entity<? extends LivingEntity>> loaded = DATABASE.executor().table("entities").select(rs -> {
                UUID uuid = UUID.fromString(rs.getString("entity_uuid"));
                Identifier id = Identifier.of(rs.getString("entity_id"));
                Entity<? extends LivingEntity> proto = Registries.ENTITIES.get(id.toString());
                if (proto == null) return null;
                Entity<? extends LivingEntity> e = proto.clone();
                e.uuid = uuid;
                return e;
            });

            loaded.forEach(e -> {
                if (e == null) return;
                ENTITIES.put(e.uuid, e);
            });

            new NaturalSpawnTask().runTaskTimer(AbyssalLib.getInstance(), 200L, 20L);
            new DespawnTask().runTaskTimer(AbyssalLib.getInstance(), 200L, 200L);
            new ChunkCacheTask().runTaskTimer(AbyssalLib.getInstance(), 0L, 40L);

        } catch (Exception e) {
            AbyssalLib.LOGGER.severe("Failed to load entity system");
            e.printStackTrace();
        }
    }

    public static void add(Entity<? extends LivingEntity> entity) {
        ENTITIES.put(entity.uuid, entity);

        entity.getBaseEntity().ifPresent(ent -> {
            World w = ent.getWorld();
            CATEGORY_COUNTS.computeIfAbsent(w, x -> new EnumMap<>(SpawnCategory.class))
                .merge(entity.getCategory(), 1, Integer::sum);

            long region = regionKey(ent.getLocation());
            REGION_DENSITY
                .computeIfAbsent(w, x -> new HashMap<>())
                .merge(region, 1, Integer::sum);
        });
        TaskUtil.delayedAsyncTask(AbyssalLib.getInstance(), 0, () -> {
            DATABASE.executor().table("entities").replace()
                .value("entity_uuid", entity.uuid.toString())
                .value("entity_id", entity.getId().toString())
                .execute();
        });
    }

    public static Entity<? extends LivingEntity> get(UUID uuid) {
        return ENTITIES.get(uuid);
    }

    public static void remove(UUID uuid) {
        Entity<? extends LivingEntity> e = ENTITIES.remove(uuid);
        if (e != null) {
            e.getBaseEntity().ifPresent(ent -> {
                World w = ent.getWorld();
                CATEGORY_COUNTS
                    .getOrDefault(w, new EnumMap<>(SpawnCategory.class))
                    .merge(e.getCategory(), -1, (a, b) -> Math.max(0, a + b));

                long region = regionKey(ent.getLocation());
                REGION_DENSITY
                    .getOrDefault(w, Map.of())
                    .merge(region, -1, (a, b) -> Math.max(0, a + b));
            });
        }

        TaskUtil.delayedAsyncTask(AbyssalLib.getInstance(), 0, () -> {
            DATABASE.executor().table("entities").delete()
                .where("entity_uuid = ?", uuid.toString())
                .execute();
        });
    }

    public static void restoreEntities() {
        for (Map.Entry<UUID, Entity<? extends LivingEntity>> entry : ENTITIES.entrySet()) {
            if (Bukkit.getEntity(entry.getKey()) != null) {
                entry.getValue().onLoad();
                entry.getValue().applyGoals();
                entry.getValue().applyAttributes();
            }
        }
        AbyssalLib.LOGGER.info("Loaded " + ENTITIES.size() + " custom entities.");
    }

    public static int count(World world, SpawnCategory category) {
        return CATEGORY_COUNTS
            .getOrDefault(world, new EnumMap<>(SpawnCategory.class))
            .getOrDefault(category, 0);
    }

    private static boolean shouldDespawn(LivingEntity entity) {
        if (!entity.getLocation().isChunkLoaded()) return false;
        if (entity.isPersistent() || entity.getCustomName() != null) return false;

        Location loc = entity.getLocation();
        World world = entity.getWorld();

        double nearest = Double.MAX_VALUE;
        for (Player p : world.getPlayers()) {
            nearest = Math.min(nearest, p.getLocation().distanceSquared(loc));
        }

        if (nearest > 128 * 128) return true;
        if (nearest > 64 * 64) return RAND.nextInt(4000) == 0;

        return false;
    }

    private static class DespawnTask extends BukkitRunnable {
        @Override
        public void run() {
            for (Entity<? extends LivingEntity> e : new ArrayList<>(ENTITIES.values())) {
                e.getBaseEntity().ifPresent(ent -> {
                    if (!shouldDespawn(ent)) return;
                    e.onUnload();
                    ent.remove();
                    remove(ent.getUniqueId());
                });
            }
        }
    }

    private static class NaturalSpawnTask extends BukkitRunnable {
        @Override
        public void run() {
            for (World world : Bukkit.getWorlds()) {
                if (world.getPlayers().isEmpty()) continue;

                List<Chunk> chunks = CHUNK_CACHE.get(world);
                if (chunks == null || chunks.isEmpty()) continue;

                List<Location> players = getPlayerLocations(world);

                for (SpawnCategory category : SpawnCategory.values()) {
                    tickCategory(world, category, chunks, players);
                }
            }
        }

        private static void tickCategory(World world, SpawnCategory category, List<Chunk> chunks, List<Location> players) {
            int cap = getLimits(category, chunks.size());
            int current = count(world, category);
            if (current >= cap) return;

            Collections.shuffle(chunks);

            for (Chunk chunk : chunks) {
                if (current >= cap) break;

                List<Entity<? extends LivingEntity>> pool = NaturalSpawnRegistry.get(category);
                if (pool.isEmpty()) continue;

                Entity<? extends LivingEntity> proto = weightedRandom(pool);
                if (proto == null) continue;

                Entity.SpawnSettings s = proto.getSpawnSettings();
                if (s == null) continue;

                Location base = randomLocation(chunk, s, players);
                if (base == null) continue;

                NamespacedKey biome = getBiomeKey(base);
                if (!s.biomes.isEmpty() && !s.biomes.contains(biome)) continue;

                long region = regionKey(base);
                int density = REGION_DENSITY.getOrDefault(world, Map.of()).getOrDefault(region, 0);
                if (density >= MAX_REGION_DENSITY) continue;

                if (!isValidSpawn(s, world, base, players)) continue;

                int pack = rand(s.minPack, s.maxPack);
                for (int i = 0; i < pack; i++) {
                    Location off = base.clone().add(
                        rand(-8, 8),
                        0,
                        rand(-8, 8)
                    );

                    Location onGround = randomLocation(chunk, s, players);
                    if (s.placement == Entity.SpawnPlacement.ON_GROUND && onGround != null) {
                        off.setY(onGround.getY());
                    }

                    if (!isValidSpawn(s, world, off, players)) continue;
                    proto.clone().spawn(off, EntitySpawnEvent.SpawnReason.NATURAL);
                    REGION_DENSITY.computeIfAbsent(world, x -> new HashMap<>())
                        .merge(region, 1, Integer::sum);
                    current++;
                }
            }
        }
    }

    private static int getLimits(SpawnCategory cat, int chunks) {
        PluginConfig.SpawnLimits l = AbyssalLib.CONFIG.spawnLimits;
        int base = switch (cat) {
            case MONSTER -> l.monster.get();
            case CREATURE -> l.creature.get();
            case AMBIENT -> l.ambient.get();
            case WATER_CREATURE -> l.waterCreature.get();
            case WATER_AMBIENT -> l.waterAmbient.get();
        };
        return Math.max(1, base * chunks / 289);
    }

    private static Location randomLocation(Chunk chunk, Entity.SpawnSettings s, List<Location> players) {
        World world = chunk.getWorld();
        for (int attempt = 0; attempt < 10; attempt++) {
            int x = (chunk.getX() << 4) + RAND.nextInt(16);
            int z = (chunk.getZ() << 4) + RAND.nextInt(16);
            int y = switch (s.heightMap) {
                case MOTION_BLOCKING -> world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING);
                case MOTION_BLOCKING_NO_LEAVES -> world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING_NO_LEAVES);
                case OCEAN_FLOOR, OCEAN_FLOOR_WG -> world.getHighestBlockYAt(x, z, HeightMap.OCEAN_FLOOR);
                case WORLD_SURFACE, WORLD_SURFACE_WG -> world.getHighestBlockYAt(x, z, HeightMap.WORLD_SURFACE);
            };

            if (s.placement == Entity.SpawnPlacement.ON_GROUND) {
                while (y > s.minY && !world.getBlockAt(x, y - 1, z).getType().isSolid()) {
                    y--;
                }
            }

            if (y < s.minY || y > s.maxY) continue;
            Location loc = new Location(world, x + 0.5, y, z + 0.5);
            if (isValidSpawn(s, world, loc, players)) return loc;
        }

        return null;
    }

    private static boolean isValidSpawn(Entity.SpawnSettings s, World world, Location loc, List<Location> players) {
        Block block = loc.getBlock();
        Block below = block.getRelative(0, -1, 0);
        int light = block.getLightLevel();
        if (light < s.minLight || light > s.maxLight) return false;
        if (s.requireSkyDarkness && world.getTime() < 13000) return false;
        if (s.requireSkyAccess && block.getLightFromSky() <= 0) return false;

        boolean valid = true;
        switch (s.placement) {
            case ON_GROUND -> {
                if (!below.getType().isSolid()) valid = false;
                for (int i = 0; i < 2; i++) {
                    if (!block.getRelative(0, i, 0).isPassable()) valid = false;
                }
                if (block.isLiquid() || block.getRelative(0, 1, 0).isLiquid()) valid = false;
            }
            case IN_WATER -> {
                if (block.getType() != Material.WATER) valid = false;
                if (block.getRelative(0, 1, 0).getType() != Material.WATER) valid = false;
            }
            case IN_LAVA -> {
                if (block.getType() != Material.LAVA) valid = false;
                if (!block.getRelative(0, 1, 0).isPassable()) valid = false;
            }
        }
        for (Location p : players) if (p.distanceSquared(loc) < 48 * 48) valid = false;
        return valid && (s.canSpawn == null || s.canSpawn.test(world, loc));
    }

    private static NamespacedKey getBiomeKey(Location loc) {
        Biome biome = loc.getBlock().getBiome();
        return RegistryAccess.registryAccess()
            .getRegistry(RegistryKey.BIOME)
            .getKey(biome);
    }

    private static List<Location> getPlayerLocations(World world) {
        List<Location> list = new ArrayList<>();
        for (Player p : world.getPlayers()) list.add(p.getLocation());
        return list;
    }

    private static long regionKey(Location loc) {
        int rx = loc.getBlockX() >> 5;
        int rz = loc.getBlockZ() >> 5;
        return (((long) rx) << 32) | (rz & 0xffffffffL);
    }

    private static int rand(int min, int max) {
        return min + RAND.nextInt(max - min + 1);
    }

    private static Entity<? extends LivingEntity> weightedRandom(List<Entity<? extends LivingEntity>> list) {
        int total = list.stream()
            .mapToInt(e -> e.getSpawnSettings().weight)
            .sum();

        int roll = RAND.nextInt(total);
        int cur = 0;

        for (Entity<? extends LivingEntity> e : list) {
            cur += e.getSpawnSettings().weight;
            if (roll < cur) return e;
        }
        return list.get(0);
    }
    private static class ChunkCacheTask extends BukkitRunnable {
        @Override
        public void run() {
            for (World world : Bukkit.getWorlds()) {
                if (world.getPlayers().isEmpty()) {
                    CHUNK_CACHE.remove(world);
                    continue;
                }

                Set<Chunk> set = new HashSet<>();
                for (Player p : world.getPlayers()) {
                    int cx = p.getLocation().getBlockX() >> 4;
                    int cz = p.getLocation().getBlockZ() >> 4;

                    for (int dx = -8; dx <= 8; dx++) {
                        for (int dz = -8; dz <= 8; dz++) {
                            if (dx * dx + dz * dz > 64) continue;
                            if (world.isChunkLoaded(cx + dx, cz + dz)) {
                                set.add(world.getChunkAt(cx + dx, cz + dz));
                            }
                        }
                    }
                }
                CHUNK_CACHE.put(world, new ArrayList<>(set));
            }
        }
    }
}