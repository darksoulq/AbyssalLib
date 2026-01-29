package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.inventory.ItemStack;

public class RepairCost extends DataComponent<Integer> implements Vanilla {
    public static final Codec<RepairCost> CODEC = Codecs.INT.xmap(
            RepairCost::new,
            RepairCost::getValue
    );
    public static final DataComponentType<RepairCost> TYPE = DataComponentType.valued(CODEC, RepairCost::new);

    public RepairCost(int cost) {
        super(cost);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.REPAIR_COST, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.REPAIR_COST);
    }
}
