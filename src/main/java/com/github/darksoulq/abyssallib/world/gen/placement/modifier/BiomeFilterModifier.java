package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A placement modifier that filters positions based on the biome at the target location.
 * <p>
 * If the biome at a given {@link Vector} is not present in the allowed list, that
 * position is discarded from the placement stream, preventing the feature from generating.
 */
public class BiomeFilterModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the biome filter modifier.
     * <p>
     * It expects a "biomes" field containing a list of namespaced strings (e.g., "minecraft:plains").
     */
    public static final Codec<BiomeFilterModifier> CODEC = new Codec<>() {
        /**
         * Decodes a BiomeFilterModifier from the provided serialized data.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input.
         * @param <D>   The data type.
         * @return A new instance of {@link BiomeFilterModifier}.
         * @throws CodecException If the "biomes" list is missing or invalid.
         */
        @Override
        public <D> BiomeFilterModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            List<String> biomes = Codecs.STRING.list().decode(ops, map.get(ops.createString("biomes")));
            return new BiomeFilterModifier(biomes);
        }

        /**
         * Encodes the BiomeFilterModifier into a serialized format.
         *
         * @param ops   The dynamic operations logic.
         * @param value The modifier instance.
         * @param <D>   The data type.
         * @return A map containing the list of valid biome strings.
         * @throws CodecException If serialization fails.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, BiomeFilterModifier value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("biomes"), Codecs.STRING.list().encode(ops, value.validBiomes));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the biome filter modifier.
     */
    public static final PlacementModifierType<BiomeFilterModifier> TYPE = () -> CODEC;

    /** The list of namespaced biome keys allowed by this filter. */
    private final List<String> validBiomes;

    /**
     * Constructs a new BiomeFilterModifier.
     *
     * @param validBiomes A list of allowed biome identifiers.
     */
    public BiomeFilterModifier(List<String> validBiomes) {
        this.validBiomes = validBiomes;
    }

    /**
     * Filters the input stream of positions, retaining only those within allowed biomes.
     * <p>
     * This method resolves the biome at each coordinate using the world's biome provider
     * and checks its namespaced key against the internal allowed list.
     * </p>
     *
     * @param context   The current {@link PlacementContext}.
     * @param positions The stream of potential placement positions.
     * @return A filtered {@link Stream} containing only positions in valid biomes.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.filter(pos -> {
            Biome biome = context.level().getWorld().getBiome(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
            Registry<Biome> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME);
            NamespacedKey key = registry.getKey(biome);
            return key != null && validBiomes.contains(key.toString());
        });
    }

    /**
     * Retrieves the specific type definition for this modifier.
     *
     * @return The {@link PlacementModifierType} associated with {@link BiomeFilterModifier}.
     */
    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}