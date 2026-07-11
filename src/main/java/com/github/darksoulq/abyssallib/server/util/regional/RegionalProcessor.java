package com.github.darksoulq.abyssallib.server.util.regional;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.reflection.Reflect;
import com.github.darksoulq.abyssallib.common.reflection.ReflectClass;
import com.github.darksoulq.abyssallib.common.reflection.ReflectMethod;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

public final class RegionalProcessor {

    private static ReflectMethod<Object> getRegionSchedulerMethod;
    private static ReflectMethod<Object> runMethod;
    private static ReflectMethod<Boolean> isOwnedMethod;

    static {
        if (RegionalCollections.IS_FOLIA) {
            ReflectClass<Bukkit> bukkitClass = Reflect.of(Bukkit.class);

            isOwnedMethod = bukkitClass.method("isOwnedByCurrentRegion", Location.class)
                .map(ReflectMethod::<Boolean>unchecked)
                .getOrNull();

            getRegionSchedulerMethod = bukkitClass.method("getRegionScheduler")
                .map(ReflectMethod::unchecked)
                .getOrNull();

            if (getRegionSchedulerMethod != null) {
                Class<?> schedulerClass = getRegionSchedulerMethod.getReturnType();
                ReflectClass<?> schedulerReflect = Reflect.of(schedulerClass);
                runMethod = schedulerReflect.method("run", Plugin.class, Location.class, Consumer.class)
                    .map(ReflectMethod::unchecked)
                    .getOrNull();
            }
        }
    }

    private RegionalProcessor() {
    }

    private static void executeOrSchedule(@NotNull Plugin plugin, @NotNull Location location, @NotNull Runnable task) {
        if (!RegionalCollections.IS_FOLIA) {
            if (Bukkit.isPrimaryThread()) {
                task.run();
            } else {
                AbyssalLib.SCHEDULER.schedule(task).once();
            }
            return;
        }

        if (isOwnedMethod != null) {
            Boolean isOwned = isOwnedMethod.invoke(null, location).getOrElse(false);
            if (Boolean.TRUE.equals(isOwned)) {
                task.run();
                return;
            }
        }

        if (getRegionSchedulerMethod != null && runMethod != null) {
            Object scheduler = getRegionSchedulerMethod.invoke(null).getOrNull();
            if (scheduler != null) {
                runMethod.invoke(scheduler, plugin, location, (Consumer<Object>) o -> task.run());
                return;
            }
        }
        task.run();
    }

    public static void processLine(
        @NotNull Plugin plugin,
        @NotNull Location start,
        @NotNull Vector direction,
        double maxDistance,
        double step,
        @NotNull Function<Block, Boolean> processor,
        @Nullable Runnable onComplete
    ) {
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(start);
        Objects.requireNonNull(direction);
        Objects.requireNonNull(processor);

        List<Location> points = new ArrayList<>();
        Vector dir = direction.clone().normalize().multiply(step);
        Location current = start.clone();

        for (double d = 0; d <= maxDistance; d += step) {
            points.add(current.clone());
            current.add(dir);
        }

        if (points.isEmpty()) {
            if (onComplete != null) onComplete.run();
            return;
        }

        List<List<Location>> segments = new ArrayList<>();
        List<Location> currentSegment = new ArrayList<>();
        int lastCx = -1;
        int lastCz = -1;

        for (Location loc : points) {
            int cx = loc.getBlockX() >> 4;
            int cz = loc.getBlockZ() >> 4;
            if (currentSegment.isEmpty() || (cx == lastCx && cz == lastCz)) {
                currentSegment.add(loc);
            } else {
                segments.add(currentSegment);
                currentSegment = new ArrayList<>();
                currentSegment.add(loc);
            }
            lastCx = cx;
            lastCz = cz;
        }

        if (!currentSegment.isEmpty()) {
            segments.add(currentSegment);
        }

        executeNextSegment(plugin, segments, 0, processor, onComplete);
    }

    private static void executeNextSegment(
        @NotNull Plugin plugin,
        @NotNull List<List<Location>> segments,
        int index,
        @NotNull Function<Block, Boolean> processor,
        @Nullable Runnable onComplete
    ) {
        if (index >= segments.size()) {
            if (onComplete != null) onComplete.run();
            return;
        }

        List<Location> segment = segments.get(index);
        Location startLoc = segment.get(0);

        executeOrSchedule(plugin, startLoc, () -> {
            for (Location loc : segment) {
                if (!processor.apply(loc.getBlock())) {
                    if (onComplete != null) onComplete.run();
                    return;
                }
            }
            executeNextSegment(plugin, segments, index + 1, processor, onComplete);
        });
    }

    public static void processVolume(
        @NotNull Plugin plugin,
        @NotNull Location corner1,
        @NotNull Location corner2,
        @NotNull Consumer<Block> processor,
        @Nullable Runnable onComplete
    ) {
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(corner1);
        Objects.requireNonNull(corner2);
        Objects.requireNonNull(processor);

        World world = corner1.getWorld();
        if (world == null || !world.equals(corner2.getWorld())) {
            throw new IllegalArgumentException();
        }

        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        int minChunkX = minX >> 4;
        int maxChunkX = maxX >> 4;
        int minChunkZ = minZ >> 4;
        int maxChunkZ = maxZ >> 4;

        int totalChunks = (maxChunkX - minChunkX + 1) * (maxChunkZ - minChunkZ + 1);
        AtomicInteger chunksRemaining = new AtomicInteger(totalChunks);

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {

                int chunkMinX = Math.max(minX, cx << 4);
                int chunkMaxX = Math.min(maxX, (cx << 4) + 15);
                int chunkMinZ = Math.max(minZ, cz << 4);
                int chunkMaxZ = Math.min(maxZ, (cz << 4) + 15);

                Location chunkLoc = new Location(world, cx << 4, 0, cz << 4);

                executeOrSchedule(plugin, chunkLoc, () -> {
                    for (int x = chunkMinX; x <= chunkMaxX; x++) {
                        for (int z = chunkMinZ; z <= chunkMaxZ; z++) {
                            for (int y = minY; y <= maxY; y++) {
                                processor.accept(world.getBlockAt(x, y, z));
                            }
                        }
                    }
                    if (chunksRemaining.decrementAndGet() == 0 && onComplete != null) {
                        onComplete.run();
                    }
                });
            }
        }
    }
}