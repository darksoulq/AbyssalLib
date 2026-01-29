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

public class LocationCheckCondition extends LootCondition {
    public static final Codec<LocationCheckCondition> CODEC = new Codec<>() {
        @Override
        public <D> LocationCheckCondition decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            List<String> biomes = Codecs.STRING.list().decode(ops, map.get(ops.createString("biomes")));
            return new LocationCheckCondition(biomes);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, LocationCheckCondition value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("biomes"), Codecs.STRING.list().encode(ops, value.biomes));
            return ops.createMap(map);
        }
    };

    public static final LootConditionType<LocationCheckCondition> TYPE = () -> CODEC;

    private final List<String> biomes;

    public LocationCheckCondition(List<String> biomes) {
        this.biomes = biomes;
    }

    @Override
    public boolean test(LootContext context) {
        Biome biome = context.location().getBlock().getBiome();
        Registry<Biome> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME);
        NamespacedKey key = registry.getKey(biome);
        return key != null && biomes.contains(key.toString());
    }

    @Override
    public LootConditionType<?> getType() {
        return TYPE;
    }
}