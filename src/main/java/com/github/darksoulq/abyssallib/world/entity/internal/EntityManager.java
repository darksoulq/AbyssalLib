package com.github.darksoulq.abyssallib.world.entity.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.config.internal.PluginConfig;
import com.github.darksoulq.abyssallib.common.database.relational.sql.Database;
import com.github.darksoulq.abyssallib.server.event.custom.entity.CustomEntitySpawnEvent;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.scheduler.Clock;
import com.github.darksoulq.abyssallib.world.entity.CustomEntity;
import com.github.darksoulq.abyssallib.world.entity.SpawnCategory;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EntityManager {
    public static final Random RAND = new Random();

    private static final Map<UUID, CustomEntity<? extends LivingEntity>> ENTITIES = new ConcurrentHashMap<>();
    private static final Map<World, Map<SpawnCategory, Integer>> CATEGORY_COUNTS = new ConcurrentHashMap<>();
    private static final Map<World, Map<Long, Integer>> REGION_DENSITY = new ConcurrentHashMap<>();
    private static final Map<World, List<Long>> CHUNK_CACHE = new ConcurrentHashMap<>();
    private static final int MAX_REGION_DENSITY = 6;

    private static final Database DATABASE = new Database(new File(AbyssalLib.getInstance().getDataFolder(), "entities.db"));

    public static void load() {
        try {
            DATABASE.connect();
            DATABASE.executor().create("entities")
                .ifNotExists()
                .column("entity_uuid", "TEXT")
                .column("entity_id", "TEXT")
                .primaryKey("entity_uuid")
                .execute();

            List<CustomEntity<? extends LivingEntity>> loaded = DATABASE.executor().table("entities").select(rs -> {
                try {
                    UUID uuid = UUID.fromString(rs.getString("entity_uuid"));
                    Key id = Key.key(rs.getString("entity_id"));
                    CustomEntity<? extends LivingEntity> proto = Registries.ENTITIES.get(id.asString());
                    if (proto == null) return null;
                    CustomEntity<? extends LivingEntity> e = proto.clone();
                    e.uuid = uuid;
                    return e;
                } catch (Exception ex) {
                    return null;
                }
            });

            loaded.forEach(e -> {
                if (e == null) return;
                ENTITIES.put(e.uuid, e);
            });

            AbyssalLib.SCHEDULER.schedule(EntityManager::naturalSpawnTick).global().after(200L, Clock.TICKS).repeatEvery(20L, Clock.TICKS);
            AbyssalLib.SCHEDULER.schedule(EntityManager::despawnTick).global().after(200L, Clock.TICKS).repeatEvery(200L, Clock.TICKS);
            AbyssalLib.SCHEDULER.schedule(EntityManager::chunkCacheTick).global().repeatEvery(40L, Clock.TICKS);

        } catch (Exception e) {
            AbyssalLib.LOGGER.severe("Failed to load entity system");
            e.printStackTrace();
        }
    }

    public static void add(CustomEntity<? extends LivingEntity> entity) {
        ENTITIES.put(entity.uuid, entity);

        entity.getBaseEntity().ifPresent(ent -> {
            World w = ent.getWorld();
            CATEGORY_COUNTS.computeIfAbsent(w, x -> new EnumMap<>(SpawnCategory.class))
                .merge(entity.getCategory(), 1, Integer::sum);

            long region = regionKey(ent.getLocation());
            REGION_DENSITY
                .computeIfAbsent(w, x -> new ConcurrentHashMap<>())
                .merge(region, 1, Integer::sum);
        });

        AbyssalLib.SCHEDULER.schedule(() -> {
            DATABASE.executor().table("entities").replace()
                .value("entity_uuid", entity.uuid.toString())
                .value("entity_id", entity.getId().toString())
                .execute();
        }).async().once();
    }

    public static CustomEntity<? extends LivingEntity> get(UUID uuid) {
        return ENTITIES.get(uuid);
    }

    public static void remove(UUID uuid) {
        CustomEntity<? extends LivingEntity> e = ENTITIES.remove(uuid);
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
        AbyssalLib.SCHEDULER.schedule(() -> {
            DATABASE.executor().table("entities").delete()
                .where("entity_uuid = ?", uuid.toString())
                .execute();
        }).async().once();
    }

    public static void restoreEntities() {
        for (Map.Entry<UUID, CustomEntity<? extends LivingEntity>> entry : ENTITIES.entrySet()) {
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
        if (entity.isPersistent() || entity.customName() != null) return false;

        Location loc = entity.getLocation();
        World world = entity.getWorld();

        double nearest = Double.MAX_VALUE;
        for (org.bukkit.entity.Entity e : world.getNearbyEntities(loc, 128, 128, 128)) {
            if (e instanceof Player p) {
                nearest = Math.min(nearest, p.getLocation().distanceSquared(loc));
            }
        }

        if (nearest > 128 * 128) return true;
        if (nearest > 64 * 64) return RAND.nextInt(4000) == 0;

        return false;
    }

    private static void despawnTick() {
        for (CustomEntity<? extends LivingEntity> e : new ArrayList<>(ENTITIES.values())) {
            e.getBaseEntity().ifPresent(ent -> {
                AbyssalLib.SCHEDULER.schedule(() -> {
                    if (!ent.isValid()) return;
                    if (!shouldDespawn(ent)) return;
                    e.onUnload();
                    ent.remove();
                    remove(ent.getUniqueId());
                }).entity(ent).once();
            });
        }
    }

    private static void naturalSpawnTick() {
        for (World world : Bukkit.getWorlds()) {
            List<Long> chunks = CHUNK_CACHE.get(world);
            if (chunks == null || chunks.isEmpty()) continue;

            for (SpawnCategory category : SpawnCategory.values()) {
                int cap = getLimits(category, chunks.size());
                int current = count(world, category);
                if (current >= cap) continue;

                List<Long> shuffledChunks = new ArrayList<>(chunks);
                Collections.shuffle(shuffledChunks);

                int needed = cap - current;
                for (int i = 0; i < Math.min(shuffledChunks.size(), needed); i++) {
                    long chunkKey = shuffledChunks.get(i);
                    int cx = (int) chunkKey;
                    int cz = (int) (chunkKey >> 32);

                    Location regionLoc = new Location(world, (cx << 4) + 8, 64, (cz << 4) + 8);

                    AbyssalLib.SCHEDULER.schedule(() -> {
                        attemptSpawnInChunk(world, cx, cz, category);
                    }).region(regionLoc).once();
                }
            }
        }
    }

    private static void attemptSpawnInChunk(World world, int cx, int cz, SpawnCategory category) {
        Location center = new Location(world, (cx << 4) + 8, 64, (cz << 4) + 8);
        if (!center.isChunkLoaded()) return;

        List<Player> nearbyPlayers = new ArrayList<>();
        for (org.bukkit.entity.Entity e : world.getNearbyEntities(center, 128, 128, 128)) {
            if (e instanceof Player p) nearbyPlayers.add(p);
        }

        if (nearbyPlayers.isEmpty()) return;

        List<CustomEntity<? extends LivingEntity>> pool = NaturalSpawnRegistry.get(category);
        if (pool.isEmpty()) return;

        CustomEntity<? extends LivingEntity> proto = weightedRandom(pool);
        if (proto == null) return;

        CustomEntity.SpawnSettings s = proto.getSpawnSettings();
        if (s == null) return;

        Location base = randomLocation(world, cx, cz, s, nearbyPlayers);
        if (base == null) return;

        NamespacedKey biome = getBiomeKey(base);
        if (!s.biomes.isEmpty() && !s.biomes.contains(biome)) return;

        long region = regionKey(base);
        int density = REGION_DENSITY.getOrDefault(world, Map.of()).getOrDefault(region, 0);
        if (density >= MAX_REGION_DENSITY) return;

        if (!isValidSpawn(s, world, base, nearbyPlayers)) return;

        int pack = rand(s.minPack, s.maxPack);
        for (int i = 0; i < pack; i++) {
            Location off = base.clone().add(
                rand(-8, 8),
                0,
                rand(-8, 8)
            );

            Location onGround = randomLocation(world, off.getBlockX() >> 4, off.getBlockZ() >> 4, s, nearbyPlayers);
            if (s.placement == CustomEntity.SpawnPlacement.ON_GROUND && onGround != null) {
                off.setY(onGround.getY());
            }

            if (!isValidSpawn(s, world, off, nearbyPlayers)) continue;

            proto.clone().spawn(off, CustomEntitySpawnEvent.SpawnReason.NATURAL);
            REGION_DENSITY.computeIfAbsent(world, x -> new ConcurrentHashMap<>())
                .merge(region, 1, Integer::sum);
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

    private static Location randomLocation(World world, int cx, int cz, CustomEntity.SpawnSettings s, List<Player> players) {
        for (int attempt = 0; attempt < 10; attempt++) {
            int x = (cx << 4) + RAND.nextInt(16);
            int z = (cz << 4) + RAND.nextInt(16);
            int y = switch (s.heightMap) {
                case MOTION_BLOCKING -> world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING);
                case MOTION_BLOCKING_NO_LEAVES -> world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING_NO_LEAVES);
                case OCEAN_FLOOR, OCEAN_FLOOR_WG -> world.getHighestBlockYAt(x, z, HeightMap.OCEAN_FLOOR);
                case WORLD_SURFACE, WORLD_SURFACE_WG -> world.getHighestBlockYAt(x, z, HeightMap.WORLD_SURFACE);
            };

            if (s.placement == CustomEntity.SpawnPlacement.ON_GROUND) {
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

    private static boolean isValidSpawn(CustomEntity.SpawnSettings s, World world, Location loc, List<Player> players) {
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
        for (Player p : players) if (p.getLocation().distanceSquared(loc) < 48 * 48) valid = false;
        return valid && (s.canSpawn == null || s.canSpawn.test(world, loc));
    }

    private static NamespacedKey getBiomeKey(Location loc) {
        Biome biome = loc.getBlock().getBiome();
        return RegistryAccess.registryAccess()
            .getRegistry(RegistryKey.BIOME)
            .getKey(biome);
    }

    private static long regionKey(Location loc) {
        int rx = loc.getBlockX() >> 5;
        int rz = loc.getBlockZ() >> 5;
        return (((long) rx) << 32) | (rz & 0xffffffffL);
    }

    private static int rand(int min, int max) {
        return min + RAND.nextInt(max - min + 1);
    }

    private static CustomEntity<? extends LivingEntity> weightedRandom(List<CustomEntity<? extends LivingEntity>> list) {
        int total = list.stream()
            .mapToInt(e -> e.getSpawnSettings() != null ? e.getSpawnSettings().weight : 0)
            .sum();

        int roll = RAND.nextInt(total);
        int cur = 0;

        for (CustomEntity<? extends LivingEntity> e : list) {
            if (e.getSpawnSettings() == null) continue;
            cur += e.getSpawnSettings().weight;
            if (roll < cur) return e;
        }
        return list.getFirst();
    }

    private static void chunkCacheTick() {
        Map<World, Set<Long>> newCache = new HashMap<>();
        for (World w : Bukkit.getWorlds()) newCache.put(w, ConcurrentHashMap.newKeySet());

        for (Player p : Bukkit.getOnlinePlayers()) {
            AbyssalLib.SCHEDULER.schedule(() -> {
                Location loc = p.getLocation();
                int cx = loc.getBlockX() >> 4;
                int cz = loc.getBlockZ() >> 4;
                World w = loc.getWorld();
                Set<Long> set = newCache.get(w);
                if (set != null) {
                    for (int dx = -8; dx <= 8; dx++) {
                        for (int dz = -8; dz <= 8; dz++) {
                            if (dx * dx + dz * dz > 64) continue;
                            if (w.isChunkLoaded(cx + dx, cz + dz)) {
                                set.add(((long) (cx + dx) & 0xFFFFFFFFL) | (((long) (cz + dz) & 0xFFFFFFFFL) << 32));
                            }
                        }
                    }
                }
            }).entity(p).once();
        }

        AbyssalLib.SCHEDULER.schedule(() -> {
            for (Map.Entry<World, Set<Long>> entry : newCache.entrySet()) {
                CHUNK_CACHE.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
        }).global().after(10L, Clock.TICKS).once();
    }
}