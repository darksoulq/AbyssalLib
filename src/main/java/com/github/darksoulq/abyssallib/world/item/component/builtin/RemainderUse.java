package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseRemainder;
import org.bukkit.inventory.ItemStack;

public class RemainderUse extends DataComponent<UseRemainder> implements Vanilla {
    public static final Codec<RemainderUse> CODEC = Codecs.ITEM_STACK.xmap(
            RemainderUse::new,
            r -> r.value.transformInto()
    );
    public static final DataComponentType<RemainderUse> TYPE = DataComponentType.valued(CODEC, RemainderUse::new);

    public RemainderUse(ItemStack remainder) {
        super(UseRemainder.useRemainder(remainder));
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
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
