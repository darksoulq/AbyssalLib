package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.Map;

public class BlockMinedCriterion implements AdvancementCriterion {

    public static final Codec<BlockMinedCriterion> CODEC = new Codec<>() {
        @Override
        public <D> BlockMinedCriterion decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow();
            Material material = Codec.enumCodec(Material.class).decode(ops, map.get(ops.createString("material")));
            int amount = Codecs.INT.decode(ops, map.get(ops.createString("amount")));
            return new BlockMinedCriterion(material, amount);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, BlockMinedCriterion value) throws CodecException {
            return ops.createMap(Map.of(
                ops.createString("material"), Codec.enumCodec(Material.class).encode(ops, value.material),
                ops.createString("amount"), Codecs.INT.encode(ops, value.amount)
            ));
        }
    };

    public static final CriterionType<BlockMinedCriterion> TYPE = () -> CODEC;

    private final Material material;
    private final int amount;

    public BlockMinedCriterion(Material material, int amount) {
        this.material = material;
        this.amount = amount;
    }

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean isMet(Player player) {
        return player.getStatistic(Statistic.MINE_BLOCK, material) >= amount;
    }
}