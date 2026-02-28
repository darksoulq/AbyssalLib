package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.feature.FeaturePlaceContext;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * A world generation feature that creates a scattered, organic pile of a specific block.
 * <p>
 * Used primarily for features like melon patches or scattered surface detritus,
 * checking to ensure it lands strictly upon dirt or grass variants.
 */
public class BlockPileFeature extends Feature<BlockPileFeature.Config> {

    /**
     * Constructs a new BlockPileFeature with the associated configuration codec.
     */
    public BlockPileFeature() {
        super(Config.CODEC);
    }

    /**
     * Executes the placement logic for the block pile.
     *
     * @param context The {@link FeaturePlaceContext} providing world access, origin, random source, and config.
     * @return {@code true} if at least one block was successfully placed; {@code false} otherwise.
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Location origin = context.origin();
        if (context.level().getType(origin.getBlockX(), origin.getBlockY(), origin.getBlockZ()) != Material.AIR) return false;

        Random random = context.random();
        int count = 0;

        for (int i = 0; i < 64; i++) {
            Location pos = origin.clone().add(
                random.nextInt(7) - random.nextInt(7),
                random.nextInt(4) - random.nextInt(4),
                random.nextInt(7) - random.nextInt(7)
            );

            if (context.level().getType(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()) != Material.AIR) continue;

            Material below = context.level().getType(pos.getBlockX(), pos.getBlockY() - 1, pos.getBlockZ());
            if (below == Material.DIRT || below == Material.GRASS_BLOCK || below == Material.PODZOL || below == Material.COARSE_DIRT || below == Material.MOSS_BLOCK) {
                WorldGenUtils.placeBlock(context.level(), pos, context.config().state);
                count++;
            }
        }

        return count > 0;
    }

    /**
     * Configuration record for {@link BlockPileFeature}.
     *
     * @param state The {@link BlockInfo} representing the block state to scatter into a pile.
     */
    public record Config(BlockInfo state) implements FeatureConfig {

        /**
         * The codec for serializing and deserializing {@link Config}.
         */
        public static final Codec<Config> CODEC = new Codec<>() {

            @Override
            public <D> Config decode(DynamicOps<D> ops, D input) throws CodecException {
                Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
                BlockInfo block = ExtraCodecs.BLOCK_INFO.decode(ops, map.get(ops.createString("state")));
                return new Config(block);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, Config value) throws CodecException {
                Map<D, D> map = new HashMap<>();
                map.put(ops.createString("state"), ExtraCodecs.BLOCK_INFO.encode(ops, value.state));
                return ops.createMap(map);
            }
        };
    }
}