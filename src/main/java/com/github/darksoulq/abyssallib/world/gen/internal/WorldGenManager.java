package com.github.darksoulq.abyssallib.world.gen.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.world.gen.CustomBlockPopulator;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacedFeature;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WorldGenManager implements Listener {

    private static final Map<String, List<PlacedFeature>> FEATURES = new ConcurrentHashMap<>();
    private static final Map<String, CustomBlockPopulator> POPULATORS = new HashMap<>();

    public static void init() {
        AbyssalLib.getInstance().getServer().getPluginManager().registerEvents(new WorldGenManager(), AbyssalLib.getInstance());
        for (World world : Bukkit.getWorlds()) {
            inject(world);
        }
    }

    public static void addFeature(String worldName, PlacedFeature feature) {
        FEATURES.computeIfAbsent(worldName, k -> new ArrayList<>()).add(feature);
        World world = Bukkit.getWorld(worldName);
        if (world != null) inject(world);
    }

    private static void inject(World world) {
        if (POPULATORS.containsKey(world.getName())) return;
        if (!FEATURES.containsKey(world.getName())) return;

        CustomBlockPopulator populator = new CustomBlockPopulator() {
            @Override
            public void generate(WorldGenAccess level, int chunkX, int chunkZ, Random random) {
                List<PlacedFeature> list = FEATURES.get(world.getName());
                if (list == null || list.isEmpty()) return;

                long seed = level.getWorld().getSeed();
                long chunkSeed = (seed ^ (chunkX * 341873128712L)) + (chunkZ * 132897987541L);
                Random chunkRandom = new Random(chunkSeed);

                for (PlacedFeature feature : list) {
                    feature.place(level, chunkRandom, chunkX, chunkZ);
                }
            }
        };
        
        world.getPopulators().add(populator);
        POPULATORS.put(world.getName(), populator);
        AbyssalLib.LOGGER.info("Added Custom Populator into world: " + world.getName());
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent e) {
        inject(e.getWorld());
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        inject(e.getWorld());
    }
}