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

public class BiomeFilterModifier extends PlacementModifier {
    public static final Codec<BiomeFilterModifier> CODEC = new Codec<>() {
        @Override
        public <D> BiomeFilterModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            List<String> biomes = Codecs.STRING.list().decode(ops, map.get(ops.createString("biomes")));
            return new BiomeFilterModifier(biomes);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, BiomeFilterModifier value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("biomes"), Codecs.STRING.list().encode(ops, value.validBiomes));
            return ops.createMap(map);
        }
    };

    public static final PlacementModifierType<BiomeFilterModifier> TYPE = () -> CODEC;

    private final List<String> validBiomes;

    public BiomeFilterModifier(List<String> validBiomes) {
        this.validBiomes = validBiomes;
    }

    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.filter(pos -> {
            Biome biome = context.level().getWorld().getBiome(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
            Registry<Biome> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME);
            NamespacedKey key = registry.getKey(biome);
            return key != null && validBiomes.contains(key.toString());
        });
    }

    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}