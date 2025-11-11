package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.inventory.ItemStack;

public class RepairCost extends DataComponent<Integer> implements Vanilla {
    private static final Codec<RepairCost> CODEC = Codecs.INT.xmap(
            RepairCost::new,
            RepairCost::getValue
    );

    public RepairCost(int cost) {
        super(Identifier.of(DataComponentTypes.REPAIR_COST.key().asString()), cost, CODEC);
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
