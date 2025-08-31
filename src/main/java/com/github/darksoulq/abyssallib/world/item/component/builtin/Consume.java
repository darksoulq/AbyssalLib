package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import org.bukkit.inventory.ItemStack;

public class Consume extends DataComponent<Consumable> implements Vanilla {
    private static final Codec<DataComponent<Consumable>> CODEC = Codec.of(null, null);

    public Consume(Consumable props) {
        super(Identifier.of(DataComponentTypes.CONSUMABLE.key().asString()), props, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.CONSUMABLE, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.CONSUMABLE);
    }
}
