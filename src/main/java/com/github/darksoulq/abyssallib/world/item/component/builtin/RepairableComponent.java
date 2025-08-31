package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Repairable;
import org.bukkit.inventory.ItemStack;

public class RepairableComponent extends DataComponent<Repairable> implements Vanilla {
    private static final Codec<DataComponent<Repairable>> CODEC = Codec.of(null, null);

    public RepairableComponent(Repairable rep) {
        super(Identifier.of(DataComponentTypes.REPAIRABLE.key().asString()), rep, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.REPAIRABLE, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.REPAIRABLE);
    }
}
