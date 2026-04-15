package com.github.darksoulq.abyssallib.world.gen.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.world.gen.feature.impl.StructureFeature;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacedFeature;
import com.github.darksoulq.abyssallib.world.gen.feature.GenerationPhase;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StructureLocator {
    public record StructureLocation(int x, int y, int z, String id, StructureRotation rotation, Mirror mirror) {}
    private static final Map<String, Map<Long, List<StructureLocation>>> CACHE = new ConcurrentHashMap<>();
    private static final ExecutorService IO_EXECUTOR = Executors.newSingleThreadExecutor();

    public static List<StructureLocation> getStructures(String world, int regionX, int regionZ) {
        long key = ((long) regionX & 0xFFFFFFFFL) | (((long) regionZ & 0xFFFFFFFFL) << 32);
        return CACHE.getOrDefault(world, Collections.emptyMap()).getOrDefault(key, Collections.emptyList());
    }

    private static long getRegionKey(int x, int z) {
        int rx = x >> 9;
        int rz = z >> 9;
        return ((long) rx & 0xFFFFFFFFL) | (((long) rz & 0xFFFFFFFFL) << 32);
    }

    public static void record(String world, int x, int y, int z, String id, StructureRotation rotation, Mirror mirror) {
        Map<GenerationPhase, List<PlacedFeature>> phases = WorldGenManager.getFeatures().get(world);

        if (phases != null && phases.values().stream()
            .flatMap(List::stream)
            .anyMatch(pf -> pf.feature().feature() instanceof StructureFeature
                && ((StructureFeature.Config) pf.feature().config()).structureId().equals(id)
                && pf.placement().stream().anyMatch(mod -> mod.getClass().getSimpleName().contains("Fixed")))) {
            return;
        }

        long key = getRegionKey(x, z);
        CACHE.computeIfAbsent(world, k -> new ConcurrentHashMap<>())
            .computeIfAbsent(key, k -> {
                List<StructureLocation> list = new CopyOnWriteArrayList<>();
                loadRegion(world, key, list);
                return list;
            }).add(new StructureLocation(x, y, z, id, rotation, mirror));

        saveRegion(world, key);
    }

    public static void clearChunk(String world, int chunkX, int chunkZ) {
        int minX = chunkX << 4;
        int minZ = chunkZ << 4;
        int maxX = minX + 15;
        int maxZ = minZ + 15;

        long key = getRegionKey(minX, minZ);

        List<StructureLocation> list = CACHE.computeIfAbsent(world, k -> new ConcurrentHashMap<>())
            .computeIfAbsent(key, k -> {
                List<StructureLocation> l = new CopyOnWriteArrayList<>();
                loadRegion(world, key, l);
                return l;
            });

        boolean removed = list.removeIf(loc -> loc.x() >= minX && loc.x() <= maxX && loc.z() >= minZ && loc.z() <= maxZ);
        if (removed) {
            saveRegion(world, key);
        }
    }

    private static File getRegionFile(String world, long key) {
        int rx = (int) (key & 0xFFFFFFFFL);
        int rz = (int) (key >>> 32);
        File dir = new File(AbyssalLib.getInstance().getDataFolder(), "cache/structure_locations/" + world);
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "r." + rx + "." + rz + ".bin");
    }

    private static void loadRegion(String world, long key, List<StructureLocation> list) {
        File file = getRegionFile(world, key);
        if (!file.exists()) return;
        try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
            int size = in.readInt();
            for (int i = 0; i < size; i++) {
                int x = in.readInt();
                int y = in.readInt();
                int z = in.readInt();
                String id = in.readUTF();
                StructureRotation rot = StructureRotation.valueOf(in.readUTF());
                Mirror mirror = Mirror.valueOf(in.readUTF());
                list.add(new StructureLocation(x, y, z, id, rot, mirror));
            }
        } catch (Exception ignored) {}
    }

    private static void saveRegion(String world, long key) {
        List<StructureLocation> currentList = CACHE.getOrDefault(world, Collections.emptyMap()).get(key);
        if (currentList == null || currentList.isEmpty()) return;
        List<StructureLocation> snapshot = new ArrayList<>(currentList);

        IO_EXECUTOR.submit(() -> {
            File file = getRegionFile(world, key);
            try (DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {
                out.writeInt(snapshot.size());
                for (StructureLocation loc : snapshot) {
                    out.writeInt(loc.x());
                    out.writeInt(loc.y());
                    out.writeInt(loc.z());
                    out.writeUTF(loc.id());
                    out.writeUTF(loc.rotation().name());
                    out.writeUTF(loc.mirror().name());
                }
            } catch (Exception e) {
                AbyssalLib.LOGGER.warning("Failed to save structure region " + key + " for world " + world);
            }
        });
    }

    public static void saveAll() {
        for (Map.Entry<String, Map<Long, List<StructureLocation>>> worldEntry : CACHE.entrySet()) {
            String world = worldEntry.getKey();
            for (Map.Entry<Long, List<StructureLocation>> regionEntry : worldEntry.getValue().entrySet()) {
                List<StructureLocation> list = regionEntry.getValue();
                if (list == null || list.isEmpty()) continue;

                File file = getRegionFile(world, regionEntry.getKey());
                try (DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {
                    out.writeInt(list.size());
                    for (StructureLocation loc : list) {
                        out.writeInt(loc.x());
                        out.writeInt(loc.y());
                        out.writeInt(loc.z());
                        out.writeUTF(loc.id());
                        out.writeUTF(loc.rotation().name());
                        out.writeUTF(loc.mirror().name());
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    public static CompletableFuture<Location> locate(World world, String structureId, Location origin, int maxRadiusChunks) {
        return CompletableFuture.supplyAsync(() -> {
            int maxRadiusBlocks = maxRadiusChunks << 4;
            int oX = origin.getBlockX();
            int oZ = origin.getBlockZ();

            int minRx = (oX - maxRadiusBlocks) >> 9;
            int maxRx = (oX + maxRadiusBlocks) >> 9;
            int minRz = (oZ - maxRadiusBlocks) >> 9;
            int maxRz = (oZ + maxRadiusBlocks) >> 9;

            Location closest = null;
            double minDistance = Double.MAX_VALUE;

            for (int rx = minRx; rx <= maxRx; rx++) {
                for (int rz = minRz; rz <= maxRz; rz++) {
                    long key = ((long) rx & 0xFFFFFFFFL) | (((long) rz & 0xFFFFFFFFL) << 32);

                    List<StructureLocation> list = CACHE.computeIfAbsent(world.getName(), k -> new ConcurrentHashMap<>())
                        .computeIfAbsent(key, k -> {
                            List<StructureLocation> l = new CopyOnWriteArrayList<>();
                            loadRegion(world.getName(), key, l);
                            return l;
                        });

                    for (StructureLocation loc : list) {
                        if (loc.id().equals(structureId)) {
                            double dist = Math.pow(loc.x() - oX, 2) + Math.pow(loc.z() - oZ, 2);
                            if (dist < minDistance) {
                                minDistance = dist;
                                closest = new Location(world, loc.x(), loc.y(), loc.z());
                            }
                        }
                    }
                }
            }
            return closest;
        });
    }
}