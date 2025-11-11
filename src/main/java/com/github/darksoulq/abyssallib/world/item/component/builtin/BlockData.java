package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BlockItemDataProperties;
import org.bukkit.inventory.ItemStack;

public class BlockData extends DataComponent<BlockItemDataProperties> implements Vanilla {
    private static final Codec<BlockData> CODEC = Codec.of(null, null);

    public BlockData(BlockItemDataProperties props) {
        super(Identifier.of(DataComponentTypes.BLOCK_DATA.key().asString()), props, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.BLOCK_DATA, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.BLOCK_DATA);
    }
}
