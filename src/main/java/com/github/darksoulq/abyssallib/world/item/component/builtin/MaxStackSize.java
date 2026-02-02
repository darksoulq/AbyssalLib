package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.inventory.ItemStack;

public class MaxStackSize extends DataComponent<Integer> implements Vanilla {
    public static final Codec<MaxStackSize> CODEC = Codecs.INT.xmap(
            MaxStackSize::new,
            MaxStackSize::getValue
    );
    public static final DataComponentType<MaxStackSize> TYPE = DataComponentType.valued(CODEC, MaxStackSize::new);

    public MaxStackSize(int value) {
        super(value);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
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
