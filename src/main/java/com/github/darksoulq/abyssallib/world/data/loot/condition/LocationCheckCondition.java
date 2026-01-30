package com.github.darksoulq.abyssallib.world.data.loot.condition;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.data.loot.LootCondition;
import com.github.darksoulq.abyssallib.world.data.loot.LootConditionType;
import com.github.darksoulq.abyssallib.world.data.loot.LootContext;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A loot condition that evaluates to true if the loot generation occurs within specific biomes.
 * <p>
 * This condition allows for biome-specific loot tables, enabling different drops based
 * on the geographical location of the event in the game world.
 * </p>
 */
public class LocationCheckCondition extends LootCondition {

    /**
     * The codec used for serializing and deserializing the location check condition.
     * <p>
     * It maps the "biomes" field, which expects a list of namespaced strings
     * representing valid biomes for this condition.
     * </p>
     */
    public static final Codec<LocationCheckCondition> CODEC = new Codec<>() {
        /**
         * Decodes a LocationCheckCondition instance from the provided serialized data.
         *
         * @param ops   The {@link DynamicOps} instance defining the data format.
         * @param input The serialized input data.
         * @param <D>   The type of the data being processed.
         * @return A new instance of {@link LocationCheckCondition}.
         * @throws CodecException If the "biomes" field is missing or invalid.
         */
        @Override
        public <D> LocationCheckCondition decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            List<String> biomes = Codecs.STRING.list().decode(ops, map.get(ops.createString("biomes")));
            return new LocationCheckCondition(biomes);
        }

        /**
         * Encodes the LocationCheckCondition instance into a serialized format.
         *
         * @param ops   The {@link DynamicOps} instance defining the data format.
         * @param value The condition instance to encode.
         * @param <D>   The type of the data being processed.
         * @return A map representing the encoded list of biome strings.
         * @throws CodecException If the encoding process fails.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, LocationCheckCondition value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("biomes"), Codecs.STRING.list().encode(ops, value.biomes));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the location check loot condition.
     */
    public static final LootConditionType<LocationCheckCondition> TYPE = () -> CODEC;

    /** The list of biome identifiers (namespaced strings) that satisfy this condition. */
    private final List<String> biomes;

    /**
     * Constructs a new LocationCheckCondition with a list of allowed biomes.
     *
     * @param biomes A list of namespaced biome keys (e.g., "minecraft:plains").
     */
    public LocationCheckCondition(List<String> biomes) {
        this.biomes = biomes;
    }

    /**
     * Tests whether the biome at the context's location is present in the allowed list.
     * <p>
     * This method retrieves the {@link Biome} at the generation coordinates and uses
     * the {@link RegistryAccess} API to resolve its {@link NamespacedKey} for comparison.
     * </p>
     *
     * @param context The {@link LootContext} providing the world coordinates.
     * @return {@code true} if the current biome is in the list; {@code false} otherwise.
     */
    @Override
    public boolean test(LootContext context) {
        Biome biome = context.location().getBlock().getBiome();
        Registry<Biome> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME);
        NamespacedKey key = registry.getKey(biome);
        return key != null && biomes.contains(key.toString());
    }

    /**
     * Retrieves the specific type definition for this loot condition.
     *
     * @return The {@link LootConditionType} associated with {@link LocationCheckCondition}.
     */
    @Override
    public LootConditionType<?> getType() {
        return TYPE;
    }
}