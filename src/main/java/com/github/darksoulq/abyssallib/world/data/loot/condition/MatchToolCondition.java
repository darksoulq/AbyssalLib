package com.github.darksoulq.abyssallib.world.data.loot.condition;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.data.loot.LootCondition;
import com.github.darksoulq.abyssallib.world.data.loot.LootConditionType;
import com.github.darksoulq.abyssallib.world.data.loot.LootContext;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class MatchToolCondition extends LootCondition {
    public static final Codec<MatchToolCondition> CODEC = new Codec<>() {
        @Override
        public <D> MatchToolCondition decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            String matName = Codecs.STRING.decode(ops, map.get(ops.createString("item")));
            return new MatchToolCondition(Material.matchMaterial(matName));
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, MatchToolCondition value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("item"), Codecs.STRING.encode(ops, value.item.name()));
            return ops.createMap(map);
        }
    };

    public static final LootConditionType<MatchToolCondition> TYPE = () -> CODEC;

    private final Material item;

    public MatchToolCondition(Material item) {
        this.item = item;
    }

    @Override
    public boolean test(LootContext context) {
        ItemStack tool = context.tool();
        return tool != null && tool.getType() == item;
    }

    @Override
    public LootConditionType<?> getType() {
        return TYPE;
    }
}