package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.block.Biome;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A placement modifier that filters positions based on the active biome at the coordinates.
 * <p>
 * This is crucial for ensuring features like desert flora only spawn in deserts,
 * or specific ore variants only spawn in badlands.
 */
public class BiomeFilterModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the biome filter modifier.
     */
    public static final Codec<BiomeFilterModifier> CODEC = new Codec<>() {

        /**
         * Decodes the modifier from a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input.
         * @param <D>   The data format type.
         * @return A new instance of the biome filter modifier.
         * @throws CodecException If the allowed_biomes list is missing.
         */
        @Override
        public <D> BiomeFilterModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            List<String> allowedBiomes = Codecs.STRING.list().decode(ops, map.get(ops.createString("allowed_biomes")));
            return new BiomeFilterModifier(allowedBiomes);
        }

        /**
         * Encodes the modifier into a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param value The modifier instance to encode.
         * @param <D>   The data format type.
         * @return The encoded data object.
         * @throws CodecException If serialization fails.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, BiomeFilterModifier value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("allowed_biomes"), Codecs.STRING.list().encode(ops, value.allowedBiomes));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the biome filter placement modifier.
     */
    public static final PlacementModifierType<BiomeFilterModifier> TYPE = () -> CODEC;

    /** The list of acceptable biome identifiers (e.g., "minecraft:plains"). */
    private final List<String> allowedBiomes;

    /**
     * Constructs a new BiomeFilterModifier.
     *
     * @param allowedBiomes A list of valid namespaced biome keys.
     */
    public BiomeFilterModifier(List<String> allowedBiomes) {
        this.allowedBiomes = allowedBiomes;
    }

    /**
     * Filters the incoming positions by checking if the biome at the vector matches the allowed list.
     *
     * @param context   The current placement context.
     * @param positions The incoming stream of potential placement vectors.
     * @return A filtered stream containing only vectors located in the specified biomes.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.filter(pos -> {
            Biome biome = context.level().getBiome(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
            String biomeKey = biome.key().toString();
            return allowedBiomes.contains(biomeKey);
        });
    }

    /**
     * Retrieves the specific type definition for this modifier.
     *
     * @return The placement modifier type associated with this biome filter modifier.
     */
    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}