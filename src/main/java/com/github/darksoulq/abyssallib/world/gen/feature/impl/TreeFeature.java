package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.entity.CustomEntity;
import com.github.darksoulq.abyssallib.world.entity.SavedEntity;
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

        if (trunkOrigin.getBlockY() + height >= context.level().getWorld().getMaxHeight()) {
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
            Location attachment = new Location(context.level().getWorld(), point.getBlockX(), point.getBlockY(), point.getBlockZ());
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
            placed.add(new Location(delegate.getWorld(), x, y, z));
        }

        /**
         * Sets a standard material and records the coordinate.
         *
         * @param x        The X coordinate.
         * @param y        The Y coordinate.
         * @param z        The Z coordinate.
         * @param material The material.
         */
        @Override public void setBlock(int x, int y, int z, @NotNull Material material) { delegate.setBlock(x, y, z, material); record(x, y, z); }

        /**
         * Sets block data and records the coordinate.
         *
         * @param x    The X coordinate.
         * @param y    The Y coordinate.
         * @param z    The Z coordinate.
         * @param data The block data.
         */
        @Override public void setBlock(int x, int y, int z, @NotNull BlockData data) { delegate.setBlock(x, y, z, data); record(x, y, z); }

        /**
         * Sets a custom block and records the coordinate.
         *
         * @param x     The X coordinate.
         * @param y     The Y coordinate.
         * @param z     The Z coordinate.
         * @param block The custom block.
         */
        @Override public void setBlock(int x, int y, int z, @NotNull CustomBlock block) { delegate.setBlock(x, y, z, block); record(x, y, z); }

        /**
         * Sets a custom block with state and records the coordinate.
         *
         * @param x     The X coordinate.
         * @param y     The Y coordinate.
         * @param z     The Z coordinate.
         * @param block The custom block.
         * @param data  The specific block data.
         */
        @Override public void setBlock(int x, int y, int z, @NotNull CustomBlock block, @NotNull BlockData data) { delegate.setBlock(x, y, z, block, data); record(x, y, z); }

        /**
         * Spawns a standard entity unmodified by the tracker.
         *
         * @param x    The X coordinate.
         * @param y    The Y coordinate.
         * @param z    The Z coordinate.
         * @param type The entity type.
         * @return The spawned entity.
         */
        @Override public @NotNull Entity addEntity(double x, double y, double z, @NotNull EntityType type) { return delegate.addEntity(x, y, z, type); }

        /**
         * Spawns a custom entity unmodified by the tracker.
         *
         * @param x      The X coordinate.
         * @param y      The Y coordinate.
         * @param z      The Z coordinate.
         * @param entity The custom entity.
         */
        @Override public void addEntity(double x, double y, double z, @NotNull CustomEntity<?> entity) { delegate.addEntity(x, y, z, entity); }

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
        @Override public @NotNull Material getType(int x, int y, int z) { return delegate.getType(x, y, z); }

        /**
         * Retrieves the block data at the coordinate unmodified by the tracker.
         *
         * @param x The X coordinate.
         * @param y The Y coordinate.
         * @param z The Z coordinate.
         * @return The block data.
         */
        @Override public @NotNull BlockData getBlockData(int x, int y, int z) { return delegate.getBlockData(x, y, z); }

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
        @Override public @NotNull Biome getBiome(int x, int y, int z) { return delegate.getBiome(x, y, z); }

        /**
         * Retrieves the highest Y coordinate unmodified by the tracker.
         *
         * @param x         The X coordinate.
         * @param z         The Z coordinate.
         * @param heightMap The heightmap parameter.
         * @return The Y coordinate.
         */
        @Override public int getHighestBlockY(int x, int z, HeightMap heightMap) { return delegate.getHighestBlockY(x, z, heightMap); }

        /**
         * Retrieves the world context unmodified by the tracker.
         *
         * @return The Bukkit world.
         */
        @Override public @NotNull World getWorld() { return delegate.getWorld(); }

        /**
         * Retrieves the random source unmodified by the tracker.
         *
         * @return The random source.
         */
        @Override public @NotNull Random getRandom() { return delegate.getRandom(); }
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
        public static final Codec<Config> CODEC = new Codec<>() {

            /**
             * Decodes tree configuration data from the parsed format.
             *
             * @param ops   The operational integration logic.
             * @param input The unparsed data array.
             * @param <D>   The format type.
             * @return The parsed tree configuration.
             * @throws CodecException Rejects invalid properties.
             */
            @Override
            public <D> Config decode(DynamicOps<D> ops, D input) throws CodecException {
                Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));

                BlockStateProvider trunkProvider = BlockStateProvider.CODEC.decode(ops, map.get(ops.createString("trunk_provider")));
                BlockStateProvider foliageProvider = BlockStateProvider.CODEC.decode(ops, map.get(ops.createString("foliage_provider")));
                BlockStateProvider dirtProvider = BlockStateProvider.CODEC.decode(ops, map.get(ops.createString("dirt_provider")));

                TrunkPlacer trunkPlacer = TrunkPlacer.CODEC.decode(ops, map.get(ops.createString("trunk_placer")));
                FoliagePlacer foliagePlacer = FoliagePlacer.CODEC.decode(ops, map.get(ops.createString("foliage_placer")));

                RootPlacer rootPlacer = null;
                D rootNode = map.get(ops.createString("root_placer"));
                if (rootNode != null) rootPlacer = RootPlacer.CODEC.decode(ops, rootNode);

                List<TreeDecorator> decorators = new ArrayList<>();
                D decNode = map.get(ops.createString("decorators"));
                if (decNode != null) decorators = TreeDecorator.CODEC.list().decode(ops, decNode);

                int baseHeight = Codecs.INT.decode(ops, map.get(ops.createString("base_height")));
                int heightRandA = Codecs.INT.decode(ops, map.get(ops.createString("height_rand_a")));
                int heightRandB = Codecs.INT.decode(ops, map.get(ops.createString("height_rand_b")));

                int foliageRadius = Codecs.INT.decode(ops, map.get(ops.createString("foliage_radius")));
                int foliageRadiusRand = Codecs.INT.decode(ops, map.get(ops.createString("foliage_radius_rand")));

                List<BlockInfo> dirtTargets = ExtraCodecs.BLOCK_INFO.list().decode(ops, map.get(ops.createString("dirt_targets")));

                return new Config(trunkProvider, foliageProvider, dirtProvider, trunkPlacer, foliagePlacer, rootPlacer, decorators, baseHeight, heightRandA, heightRandB, foliageRadius, foliageRadiusRand, dirtTargets);
            }

            /**
             * Encodes the instantiated tree configuration into a format wrapper.
             *
             * @param ops   The operational mapping processor.
             * @param value The active configuration block.
             * @param <D>   The format type.
             * @return The strictly mapped encoded translation.
             * @throws CodecException Prevents serialization failing critical logic tests.
             */
            @Override
            public <D> D encode(DynamicOps<D> ops, Config value) throws CodecException {
                Map<D, D> map = new HashMap<>();

                map.put(ops.createString("trunk_provider"), BlockStateProvider.CODEC.encode(ops, value.trunkProvider));
                map.put(ops.createString("foliage_provider"), BlockStateProvider.CODEC.encode(ops, value.foliageProvider));
                map.put(ops.createString("dirt_provider"), BlockStateProvider.CODEC.encode(ops, value.dirtProvider));

                map.put(ops.createString("trunk_placer"), TrunkPlacer.CODEC.encode(ops, value.trunkPlacer));
                map.put(ops.createString("foliage_placer"), FoliagePlacer.CODEC.encode(ops, value.foliagePlacer));

                if (value.rootPlacer != null) map.put(ops.createString("root_placer"), RootPlacer.CODEC.encode(ops, value.rootPlacer));
                if (value.decorators != null && !value.decorators.isEmpty()) map.put(ops.createString("decorators"), TreeDecorator.CODEC.list().encode(ops, value.decorators));

                map.put(ops.createString("base_height"), Codecs.INT.encode(ops, value.baseHeight));
                map.put(ops.createString("height_rand_a"), Codecs.INT.encode(ops, value.heightRandA));
                map.put(ops.createString("height_rand_b"), Codecs.INT.encode(ops, value.heightRandB));

                map.put(ops.createString("foliage_radius"), Codecs.INT.encode(ops, value.foliageRadius));
                map.put(ops.createString("foliage_radius_rand"), Codecs.INT.encode(ops, value.foliageRadiusRand));

                map.put(ops.createString("dirt_targets"), ExtraCodecs.BLOCK_INFO.list().encode(ops, value.dirtTargets));

                return ops.createMap(map);
            }
        };
    }
}