package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemContainerContents;
import org.bukkit.inventory.ItemStack;

public class Container extends DataComponent<ItemContainerContents> implements Vanilla {
    private static final Codec<DataComponent<ItemContainerContents>> CODEC = Codecs.ITEM_STACK.list().xmap(
            l -> new Container(ItemContainerContents.containerContents(l)),
            c -> c.value.contents()
    );

    public Container(ItemContainerContents contents) {
        super(Identifier.of(DataComponentTypes.CONTAINER.key().asString()), contents, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.CONTAINER, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.CONTAINER);
    }
}
