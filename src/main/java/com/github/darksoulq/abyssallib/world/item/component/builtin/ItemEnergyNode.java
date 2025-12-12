package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.energy.EnergyNode;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;

public class ItemEnergyNode extends DataComponent<EnergyNode> {
    public static final Codec<ItemEnergyNode> CODEC = new Codec<>() {
        @Override
        public <D> ItemEnergyNode decode(DynamicOps<D> ops, D input) {
            return new ItemEnergyNode(EnergyNode.deserialize(ops, input));
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, ItemEnergyNode value) {
            return value.value.serialize(ops);
        }
    };

    public ItemEnergyNode(EnergyNode container) {
        super(Identifier.of("abyssallib", "item_energy_node"), container, CODEC);
    }
}
