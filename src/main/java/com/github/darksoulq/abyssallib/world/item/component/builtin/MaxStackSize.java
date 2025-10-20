package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.inventory.ItemStack;

public class MaxStackSize extends DataComponent<Integer> implements Vanilla {
    private static final Codec<DataComponent<Integer>> CODEC = Codecs.INT.xmap(
            MaxStackSize::new,
            m -> m.value
    );

    public MaxStackSize(int value) {
        super(Identifier.of(DataComponentTypes.MAX_STACK_SIZE.key().asString()), value, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.MAX_STACK_SIZE, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.MAX_STACK_SIZE);
    }
}
