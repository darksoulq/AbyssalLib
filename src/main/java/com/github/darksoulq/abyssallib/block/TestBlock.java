package com.github.darksoulq.abyssallib.block;

import com.github.darksoulq.abyssallib.item.Item;
import com.github.darksoulq.abyssallib.item.Items;
import com.github.darksoulq.abyssallib.util.ResourceLocation;

public class TestBlock extends Block{
    /**
     * Constructs a block with the given ID.
     *
     * @param id The {@link ResourceLocation} representing the block's ID.
     */
    public TestBlock(ResourceLocation id) {
        super(id);
    }

    @Override
    public Item blockItem() {
        return Items.INVISIBLE_ITEM.get();
    }
}
