package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseRemainder;
import org.bukkit.inventory.ItemStack;

public class RemainderUse extends DataComponent<UseRemainder> implements Vanilla {
    private static final Codec<DataComponent<UseRemainder>> CODEC = Codec.of(null, null);

    public RemainderUse(UseRemainder blocks) {
        super(Identifier.of(DataComponentTypes.USE_REMAINDER.key().asString()), blocks, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.USE_REMAINDER, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.USE_REMAINDER);
    }
}
