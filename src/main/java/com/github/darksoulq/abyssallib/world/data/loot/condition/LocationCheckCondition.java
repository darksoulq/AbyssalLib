package com.github.darksoulq.abyssallib.world.data.loot.condition;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.data.loot.LootCondition;
import com.github.darksoulq.abyssallib.world.data.loot.LootConditionType;
import com.github.darksoulq.abyssallib.world.data.loot.LootContext;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;

import java.util.List;

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
    public static final Codec<LocationCheckCondition> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.STRING.list().fieldOf("biomes").forGetter(LocationCheckCondition.class, p -> p.biomes)
    ).apply(instance, LocationCheckCondition::new)).describe("LocationCheckCondition");

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