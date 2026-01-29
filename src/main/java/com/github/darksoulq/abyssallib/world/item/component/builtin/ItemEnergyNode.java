package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.energy.EnergyNode;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;

public class ItemEnergyNode extends DataComponent<EnergyNode> {
    public static final Codec<ItemEnergyNode> CODEC = EnergyNode.CODEC.xmap(ItemEnergyNode::new, ItemEnergyNode::getValue);
    public static final DataComponentType<ItemEnergyNode> TYPE = DataComponentType.simple(CODEC);

    public ItemEnergyNode(EnergyNode container) {
        super(container);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }
}