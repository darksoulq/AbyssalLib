package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemContainerContents;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Container extends DataComponent<List<ItemStack>> implements Vanilla {
    private static final Codec<Container> CODEC = Codecs.ITEM_STACK.list().xmap(
            Container::new,
            Container::getValue
    );

    public Container(ItemContainerContents contents) {
        super(Identifier.of(DataComponentTypes.CONTAINER.key().asString()), contents.contents(), CODEC);
    }
    public Container(List<ItemStack> contents) {
        super(Identifier.of(DataComponentTypes.CONTAINER.key().asString()), contents, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.CONTAINER, ItemContainerContents.containerContents().addAll(value).build());
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.CONTAINER);
    }
}
