package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemContainerContents;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Container extends DataComponent<List<ItemStack>> implements Vanilla {
    public static final Codec<Container> CODEC = Codecs.ITEM_STACK.list().xmap(
            Container::new,
            Container::getValue
    );
    public static final DataComponentType<Container> TYPE = DataComponentType.valued(CODEC, v -> new Container((ItemContainerContents) v));

    public Container(ItemContainerContents contents) {
        super(contents.contents());
    }
    public Container(List<ItemStack> contents) {
        super(contents);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
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
