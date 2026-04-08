package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.util.Try;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class LocationCriterion implements AdvancementCriterion {

    public static final Codec<LocationCriterion> CODEC = new Codec<>() {
        @Override
        public <D> LocationCriterion decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow();
            String worldName = Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("world")))).orElse(null);
            Biome biome = Try.of(() -> {
                String biomeKey = Codecs.STRING.decode(ops, map.get(ops.createString("biome")));
                return RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME).get(Key.key(biomeKey));
            }).orElse(null);
            return new LocationCriterion(worldName, biome);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, LocationCriterion value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            if (value.worldName != null) map.put(ops.createString("world"), Codecs.STRING.encode(ops, value.worldName));
            if (value.biome != null) map.put(ops.createString("biome"), Codecs.STRING.encode(ops, value.biome.getKey().toString()));
            return ops.createMap(map);
        }
    };

    public static final CriterionType<LocationCriterion> TYPE = () -> CODEC;

    private final String worldName;
    private final Biome biome;

    public LocationCriterion(String worldName, Biome biome) {
        this.worldName = worldName;
        this.biome = biome;
    }

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean isMet(Player player) {
        if (worldName != null && !player.getWorld().getName().equals(worldName)) return false;
        return biome == null || player.getLocation().getBlock().getBiome() == biome;
    }
}