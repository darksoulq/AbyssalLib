package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.inventory.ItemStack;

public class MaxDurability extends DataComponent<Integer> implements Vanilla {
    public static final Codec<MaxDurability> CODEC = Codecs.INT.xmap(
            MaxDurability::new,
            MaxDurability::getValue
    );
    public static final DataComponentType<MaxDurability> TYPE = DataComponentType.valued(CODEC, MaxDurability::new);

    public MaxDurability(int value) {
        super(value);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.MAX_DAMAGE, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.MAX_DAMAGE);
    }
}
