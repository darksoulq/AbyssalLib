package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

/**
 * An advancement criterion evaluating the physical location (World and Biome) of the player.
 */
public class LocationCriterion implements AdvancementCriterion {

    /**
     * An internal codec for mapping the namespaced biome string directly to a Bukkit Biome instance securely.
     */
    private static final Codec<Biome> BIOME_CODEC = Codecs.STRING.flatXmap(
        biomeStr -> {
            Biome b = RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME).get(Key.key(biomeStr));
            return b != null ? DataResult.success(b) : DataResult.error("Unknown biome: " + biomeStr);
        },
        biome -> DataResult.success(biome.getKey().asString())
    ).describe("Biome");

    /**
     * The codec used for serializing and deserializing the location criterion.
     */
    public static final Codec<LocationCriterion> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.STRING.nullable().optionalFieldOf("world", null).forGetter(LocationCriterion.class, p -> p.worldName),
        BIOME_CODEC.nullable().optionalFieldOf("biome", null).forGetter(LocationCriterion.class, p -> p.biome)
    ).apply(instance, LocationCriterion::new)).describe("LocationCriterion");

    /**
     * The registered type definition for the location criterion.
     */
    public static final CriterionType<LocationCriterion> TYPE = () -> CODEC;

    private final String worldName;
    private final Biome biome;

    /**
     * Constructs a new LocationCriterion.
     *
     * @param worldName The name of the required world (nullable).
     * @param biome     The required biome (nullable).
     */
    public LocationCriterion(String worldName, Biome biome) {
        this.worldName = worldName;
        this.biome = biome;
    }

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    /**
     * Checks if the player is currently inside the required world and biome.
     *
     * @param player The player to evaluate.
     * @return True if the condition is met.
     */
    @Override
    public boolean isMet(Player player) {
        if (worldName != null && !player.getWorld().getName().equals(worldName)) return false;
        return biome == null || player.getLocation().getBlock().getBiome() == biome;
    }
}