package com.github.darksoulq.abyssallib.world.gen.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.world.gen.CustomBlockPopulator;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.feature.GenerationPhase;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacedFeature;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the injection of custom block populators into Bukkit worlds and orchestrates
 * the phase-ordered execution of registered features during chunk generation.
 */
public class WorldGenManager implements Listener {

    /**
     * A thread-safe map storing worlds mapped to an EnumMap of generation phases.
     */
    private static final Map<String, Map<GenerationPhase, List<PlacedFeature>>> FEATURES = new ConcurrentHashMap<>();

    /**
     * A map storing active custom block populators assigned to specific worlds.
     */
    private static final Map<String, CustomBlockPopulator> POPULATORS = new HashMap<>();

    /**
     * Initializes the world generation manager and registers events to inject populators.
     */
    public static void init() {
        AbyssalLib.getInstance().getServer().getPluginManager().registerEvents(new WorldGenManager(), AbyssalLib.getInstance());
        for (World world : Bukkit.getWorlds()) {
            inject(world);
        }
    }

    /**
     * Registers a placed feature to a specific world and sorts it into its phase.
     *
     * @param worldName The name of the target world.
     * @param feature   The feature to schedule.
     */
    @SuppressWarnings("unchecked")
    public static void addFeature(String worldName, PlacedFeature feature) {
        Feature<FeatureConfig> rawFeature = (Feature<FeatureConfig>) feature.feature().feature();
        GenerationPhase phase = rawFeature.getPhase(feature.feature().config());

        FEATURES.computeIfAbsent(worldName, k -> new EnumMap<>(GenerationPhase.class))
            .computeIfAbsent(phase, k -> new ArrayList<>())
            .add(feature);

        World world = Bukkit.getWorld(worldName);
        if (world != null) inject(world);
    }

    /**
     * Injects the unified custom block populator into the specified world if required.
     *
     * @param world The target world.
     */
    public static void inject(World world) {
        if (POPULATORS.containsKey(world.getName())) return;
        if (!FEATURES.containsKey(world.getName())) return;

        CustomBlockPopulator populator = new CustomBlockPopulator() {
            @Override
            public void generate(WorldGenAccess level, int chunkX, int chunkZ, Random random) {
                Map<GenerationPhase, List<PlacedFeature>> phaseMap = FEATURES.get(world.getName());
                if (phaseMap == null || phaseMap.isEmpty()) return;

                long seed = level.getWorld().getSeed();
                long chunkSeed = (seed ^ (chunkX * 341873128712L)) + (chunkZ * 132897987541L);
                Random chunkRandom = new Random(chunkSeed);

                for (GenerationPhase phase : GenerationPhase.values()) {
                    List<PlacedFeature> featuresInPhase = phaseMap.get(phase);

                    if (featuresInPhase != null) {
                        for (PlacedFeature feature : featuresInPhase) {
                            feature.place(level, chunkRandom, chunkX, chunkZ);
                        }
                    }
                }
            }
        };

        world.getPopulators().add(populator);
        POPULATORS.put(world.getName(), populator);
        AbyssalLib.LOGGER.info("Added Custom Populator into world: " + world.getName());
    }
}