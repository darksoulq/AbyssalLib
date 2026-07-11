package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.entity.CustomEntity;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.feature.FeaturePlaceContext;
import com.github.darksoulq.abyssallib.world.gen.feature.GenerationPhase;
import com.github.darksoulq.abyssallib.world.gen.feature.tree.decorator.TreeDecorator;
import com.github.darksoulq.abyssallib.world.gen.feature.tree.foliage.FoliagePlacer;
import com.github.darksoulq.abyssallib.world.gen.feature.tree.root.RootPlacer;
import com.github.darksoulq.abyssallib.world.gen.feature.tree.trunk.TrunkPlacer;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Procedural feature managing the algorithmic construction of complex, modular trees.
 */
public class TreeFeature extends Feature<TreeFeature.Config> {

    /**
     * Constructs a new TreeFeature initialized with its corresponding configuration codec.
     */
    public TreeFeature() {
        super(Config.CODEC);
    }

    /**
     * Specifies the procedural generation phase in which this feature executes.
     *
     * @param config The configuration to evaluate.
     * @return The VEGETAL_DECORATION generation phase.
     */
    @Override
    public GenerationPhase getPhase(Config config) {
        return GenerationPhase.VEGETAL_DECORATION;
    }

    /**
     * Executes the tree generation algorithm combining roots, trunks, foliage, and decorators.
     *
     * @param context The feature placement context governing world rules.
     * @return True if the tree generated successfully.
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Location origin = context.origin();
        Config config = context.config();
        Random random = context.random();

        Location ground = origin.clone().add(0, -1, 0);
        if (!WorldGenUtils.isValidBlock(context.level(), ground, config.dirtTargets())) {
            return false;
        }

        Location trunkOrigin = origin;
        if (config.rootPlacer() != null) {
            trunkOrigin = config.rootPlacer().placeRoots(context.level(), random, origin, config.trunkProvider(), config.dirtProvider());
        }

        int height = config.baseHeight() + random.nextInt(Math.max(1, config.heightRandA() + 1)) + random.nextInt(Math.max(1, config.heightRandB() + 1));

        if (trunkOrigin.getBlockY() + height >= context.level().world().getMaxHeight()) {
            return false;
        }

        if (config.rootPlacer() == null) {
            BlockInfo dirtState = config.dirtProvider().getState(random, ground);
            if (dirtState != null) {
                WorldGenUtils.placeBlock(context.level(), ground, dirtState);
            }
        }

        TrackingWorldGenAccess logTracker = new TrackingWorldGenAccess(context.level());
        TrackingWorldGenAccess leafTracker = new TrackingWorldGenAccess(context.level());

        List<Vector> foliagePoints = config.trunkPlacer().placeTrunk(logTracker, random, trunkOrigin, config.trunkProvider(), height);
        int radius = config.foliageRadius() + random.nextInt(Math.max(1, config.foliageRadiusRand() + 1));

        for (Vector point : foliagePoints) {
            Location attachment = new Location(context.level().world(), point.getBlockX(), point.getBlockY(), point.getBlockZ());
            config.foliagePlacer().placeFoliage(leafTracker, random, attachment, config.foliageProvider(), radius);
        }

        if (config.decorators() != null) {
            for (TreeDecorator decorator : config.decorators()) {
                decorator.decorate(context.level(), random, logTracker.getPlaced(), leafTracker.getPlaced());
            }
        }

        return true;
    }

    /**
     * An internal wrapper caching all coordinates modified during generation for decorators to reference.
     */
    private static class TrackingWorldGenAccess implements WorldGenAccess {

        /**
         * The standard world generation accessor being wrapped.
         */
        private final WorldGenAccess delegate;

        /**
         * The set of modified coordinates.
         */
        private final Set<Location> placed = new HashSet<>();

        /**
         * Constructs a tracking accessor.
         *
         * @param delegate The underlying world generation interface.
         */
        public TrackingWorldGenAccess(WorldGenAccess delegate) {
            this.delegate = delegate;
        }

        /**
         * Retrieves the set of all modified coordinates.
         *
         * @return The location cache.
         */
        public Set<Location> getPlaced() {
            return placed;
        }

        /**
         * Appends a coordinate explicitly to the tracking set.
         *
         * @param x The X coordinate.
         * @param y The Y coordinate.
         * @param z The Z coordinate.
         */
        private void record(int x, int y, int z) {
            placed.add(new Location(delegate.world(), x, y, z));
        }

        /**
         * Sets a standard material and records the coordinate.
         *
         * @param x        The X coordinate.
         * @param y        The Y coordinate.
         * @param z        The Z coordinate.
         * @param material The material.
         */
        @Override
        public void setBlock(int x, int y, int z, @NotNull Material material) {
            delegate.setBlock(x, y, z, material);
            record(x, y, z);
        }

        /**
         * Sets block data and records the coordinate.
         *
         * @param x    The X coordinate.
         * @param y    The Y coordinate.
         * @param z    The Z coordinate.
         * @param data The block data.
         */
        @Override
        public void setBlock(int x, int y, int z, @NotNull BlockData data) {
            delegate.setBlock(x, y, z, data);
            record(x, y, z);
        }

        /**
         * Sets a custom block and records the coordinate.
         *
         * @param x     The X coordinate.
         * @param y     The Y coordinate.
         * @param z     The Z coordinate.
         * @param block The custom block.
         */
        @Override
        public void setBlock(int x, int y, int z, @NotNull CustomBlock block) {
            delegate.setBlock(x, y, z, block);
            record(x, y, z);
        }

        /**
         * Sets a custom block with state and records the coordinate.
         *
         * @param x     The X coordinate.
         * @param y     The Y coordinate.
         * @param z     The Z coordinate.
         * @param block The custom block.
         * @param data  The specific block data.
         */
        @Override
        public void setBlock(int x, int y, int z, @NotNull CustomBlock block, @NotNull BlockData data) {
            delegate.setBlock(x, y, z, block, data);
            record(x, y, z);
        }

        /**
         * Spawns a standard entity unmodified by the tracker.
         *
         * @param x    The X coordinate.
         * @param y    The Y coordinate.
         * @param z    The Z coordinate.
         * @param type The entity type.
         * @return The spawned entity.
         */
        @Override
        public @NotNull Entity addEntity(double x, double y, double z, @NotNull EntityType type) {
            return delegate.addEntity(x, y, z, type);
        }

        /**
         * Spawns a custom entity unmodified by the tracker.
         *
         * @param x      The X coordinate.
         * @param y      The Y coordinate.
         * @param z      The Z coordinate.
         * @param entity The custom entity.
         */
        @Override
        public void addEntity(double x, double y, double z, @NotNull CustomEntity<?> entity) {
            delegate.addEntity(x, y, z, entity);
        }

        @Override
        public @Nullable Entity addEntity(double x, double y, double z, @NotNull SavedEntity entity) {
            return delegate.addEntity(x, y, z, entity);
        }

        /**
         * Retrieves the material at the coordinate unmodified by the tracker.
         *
         * @param x The X coordinate.
         * @param y The Y coordinate.
         * @param z The Z coordinate.
         * @return The material.
         */
        @Override
        public @NotNull Material getType(int x, int y, int z) {
            return delegate.getType(x, y, z);
        }

        /**
         * Retrieves the block data at the coordinate unmodified by the tracker.
         *
         * @param x The X coordinate.
         * @param y The Y coordinate.
         * @param z The Z coordinate.
         * @return The block data.
         */
        @Override
        public @NotNull BlockData getBlockData(int x, int y, int z) {
            return delegate.getBlockData(x, y, z);
        }

        @Override
        public @NotNull BlockState getBlockState(int x, int y, int z) {
            return delegate.getBlockState(x, y, z);
        }

        /**
         * Retrieves the biome at the coordinate unmodified by the tracker.
         *
         * @param x The X coordinate.
         * @param y The Y coordinate.
         * @param z The Z coordinate.
         * @return The biome.
         */
        @Override
        public @NotNull Biome getBiome(int x, int y, int z) {
            return delegate.getBiome(x, y, z);
        }

        /**
         * Retrieves the highest Y coordinate unmodified by the tracker.
         *
         * @param x         The X coordinate.
         * @param z         The Z coordinate.
         * @param heightMap The heightmap parameter.
         * @return The Y coordinate.
         */
        @Override
        public int getHighestBlockY(int x, int z, HeightMap heightMap) {
            return delegate.getHighestBlockY(x, z, heightMap);
        }

        /**
         * Retrieves the world context unmodified by the tracker.
         *
         * @return The Bukkit world.
         */
        @Override
        public @NotNull World world() {
            return delegate.world();
        }

        /**
         * Retrieves the random source unmodified by the tracker.
         *
         * @return The random source.
         */
        @Override
        public @NotNull Random random() {
            return delegate.random();
        }
    }

    /**
     * Structure detailing the placement algorithms utilized by the tree feature.
     *
     * @param trunkProvider     The provider determining log materials.
     * @param foliageProvider   The provider determining leaf materials.
     * @param dirtProvider      The provider enforcing root structural substrates.
     * @param trunkPlacer       The algorithmic framework charting trunk ascension.
     * @param foliagePlacer     The algorithmic framework charting foliage aggregation.
     * @param rootPlacer        The algorithmic framework mapping subterranean roots.
     * @param decorators        The sequential post-assembly aesthetic mutators.
     * @param baseHeight        The minimum guaranteed vertical limit of the base trunk array.
     * @param heightRandA       The primary integer scalar randomizing vertical magnitude.
     * @param heightRandB       The secondary integer scalar randomizing vertical magnitude.
     * @param foliageRadius     The minimum guaranteed horizontal limit spanning the base foliage array.
     * @param foliageRadiusRand The integer scalar randomizing horizontal magnitude encompassing the foliage canopy.
     * @param dirtTargets       The categorical whitelist determining valid surfaces permitting organic origin points.
     */
    public record Config(
        BlockStateProvider trunkProvider,
        BlockStateProvider foliageProvider,
        BlockStateProvider dirtProvider,
        TrunkPlacer trunkPlacer,
        FoliagePlacer foliagePlacer,
        RootPlacer rootPlacer,
        List<TreeDecorator> decorators,
        int baseHeight,
        int heightRandA,
        int heightRandB,
        int foliageRadius,
        int foliageRadiusRand,
        List<BlockInfo> dirtTargets
    ) implements FeatureConfig {

        /**
         * Codec responsible for serializing and deserializing the config parameters.
         */
        public static final Codec<Config> CODEC = RecordBuilder.create(instance -> instance.group(
            BlockStateProvider.CODEC.fieldOf("trunk_provider").forGetter(Config.class, Config::trunkProvider),
            BlockStateProvider.CODEC.fieldOf("foliage_provider").forGetter(Config.class, Config::foliageProvider),
            BlockStateProvider.CODEC.fieldOf("dirt_provider").forGetter(Config.class, Config::dirtProvider),
            TrunkPlacer.CODEC.fieldOf("trunk_placer").forGetter(Config.class, Config::trunkPlacer),
            FoliagePlacer.CODEC.fieldOf("foliage_placer").forGetter(Config.class, Config::foliagePlacer),
            RootPlacer.CODEC.nullable().optionalFieldOf("root_placer", null).forGetter(Config.class, Config::rootPlacer),
            TreeDecorator.CODEC.list().optionalFieldOf("decorators", Collections.emptyList()).forGetter(Config.class, Config::decorators),
            Codecs.INT.fieldOf("base_height").forGetter(Config.class, Config::baseHeight),
            Codecs.INT.fieldOf("height_rand_a").forGetter(Config.class, Config::heightRandA),
            Codecs.INT.fieldOf("height_rand_b").forGetter(Config.class, Config::heightRandB),
            Codecs.INT.fieldOf("foliage_radius").forGetter(Config.class, Config::foliageRadius),
            Codecs.INT.fieldOf("foliage_radius_rand").forGetter(Config.class, Config::foliageRadiusRand),
            ExtraCodecs.BLOCK_INFO.list().fieldOf("dirt_targets").forGetter(Config.class, Config::dirtTargets)
        ).apply(instance, Config::new)).describe("TreeFeatureConfig");
    }
}