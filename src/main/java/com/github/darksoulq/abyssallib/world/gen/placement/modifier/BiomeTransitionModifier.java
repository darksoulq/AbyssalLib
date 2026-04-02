package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.block.Biome;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.stream.Stream;

/**
 * A placement modifier that prevents feature bleeding across chunk boundaries.
 * <p>
 * Features are often scattered outwards from the center of a chunk. If a feature
 * scatters far enough, it may land in a neighboring chunk that has a completely
 * different biome. This filter ensures that the exact target block's biome
 * strictly matches the biome at the center of the generating chunk.
 */
public class BiomeTransitionModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the biome transition modifier.
     * Since this modifier has no configurable parameters, it encodes to an empty object.
     */
    public static final Codec<BiomeTransitionModifier> CODEC = new Codec<>() {

        /**
         * Decodes the empty configuration map into a new modifier.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input.
         * @param <D>   The data format type.
         * @return A new instance of the biome transition modifier.
         */
        @Override
        public <D> BiomeTransitionModifier decode(DynamicOps<D> ops, D input) {
            return new BiomeTransitionModifier();
        }

        /**
         * Encodes the modifier into an empty configuration map.
         *
         * @param ops   The dynamic operations logic.
         * @param value The modifier instance to encode.
         * @param <D>   The data format type.
         * @return The encoded empty map data object.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, BiomeTransitionModifier value) {
            return ops.createMap(new HashMap<>());
        }
    };

    /**
     * The registered type definition for the biome transition placement modifier.
     */
    public static final PlacementModifierType<BiomeTransitionModifier> TYPE = () -> CODEC;

    /**
     * Constructs a new BiomeTransitionModifier.
     */
    public BiomeTransitionModifier() {}

    /**
     * Filters the incoming positions by ensuring their exact biome matches the
     * biome recorded at the geometric center of the active generation chunk.
     *
     * @param context   The current placement context.
     * @param positions The incoming stream of potential placement vectors.
     * @return A filtered stream of vectors residing within the correct biome.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        int centerX = (context.chunkX() << 4) + 8;
        int centerZ = (context.chunkZ() << 4) + 8;
        Biome originBiome = context.level().getBiome(centerX, 0, centerZ);

        return positions.filter(pos -> {
            Biome posBiome = context.level().getBiome(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
            return posBiome == originBiome;
        });
    }

    /**
     * Retrieves the specific type definition for this modifier.
     *
     * @return The placement modifier type associated with this biome transition modifier.
     */
    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}